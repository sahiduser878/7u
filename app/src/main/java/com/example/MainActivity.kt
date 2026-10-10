package com.example

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AssistantState
import com.example.service.ZoyaWakeWordService
import com.example.ui.components.ZoyaBottomNav
import com.example.ui.screens.ChatHistoryScreen
import com.example.ui.screens.CommandsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ListeningScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SpeakingScreen
import com.example.ui.screens.YouTubeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ZoyaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ZoyaViewModel by viewModels()

    private var wakeWordReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if launched from Wake Word service intent
        if (intent?.getBooleanExtra("EXTRA_START_LISTENING", false) == true) {
            viewModel.startListening()
        }

        // Register broadcast listener for wake word triggers while activity is in foreground
        wakeWordReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ZoyaWakeWordService.ACTION_WAKE_WORD_TRIGGERED) {
                    viewModel.startListening()
                }
            }
        }
        val filter = IntentFilter(ZoyaWakeWordService.ACTION_WAKE_WORD_TRIGGERED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(wakeWordReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(wakeWordReceiver, filter)
        }

        setContent {
            MyApplicationTheme {
                ZoyaApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("EXTRA_START_LISTENING", false)) {
            viewModel.startListening()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            wakeWordReceiver?.let { unregisterReceiver(it) }
        } catch (_: Exception) {}
    }
}

@Composable
fun ZoyaApp(viewModel: ZoyaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val assistantState by viewModel.assistantState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val recentCommands by viewModel.recentCommands.collectAsStateWithLifecycle()
    val popularCommands by viewModel.popularCommands.collectAsStateWithLifecycle()
    val activePreview by viewModel.activeActionPreview.collectAsStateWithLifecycle()
    val activeYouTubeQuery by viewModel.activeYouTubeQuery.collectAsStateWithLifecycle()
    val youtubeVideos by viewModel.youtubeVideos.collectAsStateWithLifecycle()

    // Runtime Permission Launcher for Audio & Notifications
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val micGranted = result[Manifest.permission.RECORD_AUDIO] ?: false
        val notifGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result[Manifest.permission.POST_NOTIFICATIONS] ?: false
        } else {
            true
        }
        viewModel.updatePermissions(micGranted, notifGranted)
    }

    // Launch permission request on startup if needed
    LaunchedEffect(Unit) {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionsLauncher.launch(permissions.toTypedArray())
    }

    // Back button handling
    BackHandler(enabled = currentScreen != "home") {
        when (currentScreen) {
            "listening" -> viewModel.stopListening()
            "speaking" -> viewModel.stopSpeaking()
            else -> viewModel.navigateTo("home")
        }
    }

    val showBottomBar = currentScreen in listOf("home", "commands", "settings", "chats")

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (showBottomBar) {
                ZoyaBottomNav(
                    currentScreen = currentScreen,
                    onTabSelected = { targetScreen ->
                        viewModel.navigateTo(targetScreen)
                    },
                    onMicClick = {
                        viewModel.startListening()
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> {
                    HomeScreen(
                        settings = settings,
                        recentCommands = recentCommands,
                        onMicClick = { viewModel.startListening() },
                        onQuickActionClick = { prompt ->
                            viewModel.executePresetCommand(
                                com.example.model.VoiceCommandItem(
                                    id = "q",
                                    category = com.example.model.CommandCategory.ALL,
                                    commandText = prompt,
                                    translatedHint = prompt,
                                    iconType = "mic",
                                    actionType = com.example.model.ActionType.GENERAL_CONVERSATION
                                )
                            )
                        },
                        onNavigateToCommands = { viewModel.navigateTo("commands") },
                        onNavigateToSettings = { viewModel.navigateTo("settings") },
                        onNavigateToYouTube = { viewModel.navigateTo("youtube") },
                        onToggleWakeWord = { enabled -> viewModel.toggleWakeWord(enabled) }
                    )
                }

                "listening" -> {
                    ListeningScreen(
                        state = assistantState,
                        onCloseClick = { viewModel.stopListening() },
                        onStopListeningClick = { viewModel.stopListening() }
                    )
                }

                "speaking" -> {
                    val speakingState = assistantState as? AssistantState.Speaking
                        ?: AssistantState.Speaking(speechText = "Here is your result.", actionPreview = activePreview)
                    SpeakingScreen(
                        state = speakingState,
                        actionPreview = activePreview,
                        onCloseClick = { viewModel.stopSpeaking() },
                        onStopSpeakingClick = { viewModel.stopSpeaking() },
                        onToggleMuteClick = { viewModel.toggleMute() },
                        onNavigateToYouTube = { viewModel.navigateTo("youtube") }
                    )
                }

                "commands" -> {
                    CommandsScreen(
                        commands = popularCommands,
                        onBackClick = { viewModel.navigateTo("home") },
                        onCommandClick = { cmd -> viewModel.executePresetCommand(cmd) }
                    )
                }

                "settings" -> {
                    SettingsScreen(
                        settings = settings,
                        onBackClick = { viewModel.navigateTo("home") },
                        onToggleWakeWord = { enabled -> viewModel.toggleWakeWord(enabled) },
                        onLanguageSelected = { lang -> viewModel.setLanguage(lang) },
                        onVoiceSelected = { voice -> viewModel.setVoice(voice) },
                        onApiKeySaved = { key -> viewModel.setCustomApiKey(key) },
                        onRequestPermissions = {
                            val perms = mutableListOf(Manifest.permission.RECORD_AUDIO)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                perms.add(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            permissionsLauncher.launch(perms.toTypedArray())
                        }
                    )
                }

                "youtube" -> {
                    YouTubeScreen(
                        queryTitle = activeYouTubeQuery,
                        videos = youtubeVideos,
                        onBackClick = { viewModel.navigateTo("home") },
                        onLaunchYouTubeApp = { query ->
                            viewModel.searchYouTubeDirect(query)
                        }
                    )
                }

                "chats" -> {
                    ChatHistoryScreen(
                        messages = recentCommands,
                        onBackClick = { viewModel.navigateTo("home") },
                        onSendMessage = { prompt ->
                            viewModel.executePresetCommand(
                                com.example.model.VoiceCommandItem(
                                    id = "msg_${System.currentTimeMillis()}",
                                    category = com.example.model.CommandCategory.ALL,
                                    commandText = prompt,
                                    translatedHint = prompt,
                                    iconType = "mic",
                                    actionType = com.example.model.ActionType.GENERAL_CONVERSATION
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
