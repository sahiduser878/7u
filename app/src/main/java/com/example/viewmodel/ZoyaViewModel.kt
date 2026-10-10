package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.actions.ActionExecutionResult
import com.example.actions.ActionRegistry
import com.example.ai.GeminiResponseResult
import com.example.ai.GeminiService
import com.example.ai.NaturalCommandParser
import com.example.audio.AudioVoiceEngine
import com.example.model.ActionPreview
import com.example.model.ActionStatus
import com.example.model.ActionType
import com.example.model.AssistantState
import com.example.model.CommandCategory
import com.example.model.RecentCommandRecord
import com.example.model.VoiceCommandItem
import com.example.model.YouTubeVideo
import com.example.model.ZoyaSettings
import com.example.service.ZoyaWakeWordService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ZoyaViewModel(application: Application) : AndroidViewModel(application) {

    private val actionRegistry = ActionRegistry(application)
    private val geminiService = GeminiService()

    private val _assistantState = MutableStateFlow<AssistantState>(AssistantState.Idle)
    val assistantState: StateFlow<AssistantState> = _assistantState.asStateFlow()

    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _settings = MutableStateFlow(ZoyaSettings())
    val settings: StateFlow<ZoyaSettings> = _settings.asStateFlow()

    private val _activeActionPreview = MutableStateFlow<ActionPreview?>(null)
    val activeActionPreview: StateFlow<ActionPreview?> = _activeActionPreview.asStateFlow()

    private val _recentCommands = MutableStateFlow<List<RecentCommandRecord>>(
        listOf(
            RecentCommandRecord(
                id = "1",
                prompt = "YouTube par Honey Singh ka gaana chalao",
                response = "Opening YouTube and searching for Honey Singh songs...",
                timeAgo = "9:40 AM",
                status = ActionStatus.SUCCESS,
                iconType = "youtube"
            ),
            RecentCommandRecord(
                id = "2",
                prompt = "WhatsApp kholo",
                response = "Opening WhatsApp.",
                timeAgo = "9:35 AM",
                status = ActionStatus.SUCCESS,
                iconType = "whatsapp"
            ),
            RecentCommandRecord(
                id = "3",
                prompt = "Kal subah 6 baje alarm lagao",
                response = "Alarm set for 6:00 AM.",
                timeAgo = "9:20 AM",
                status = ActionStatus.SUCCESS,
                iconType = "alarm"
            ),
            RecentCommandRecord(
                id = "4",
                prompt = "Google par weather search karo",
                response = "Searching Google for weather in Kolkata.",
                timeAgo = "9:10 AM",
                status = ActionStatus.SUCCESS,
                iconType = "chrome"
            )
        )
    )
    val recentCommands: StateFlow<List<RecentCommandRecord>> = _recentCommands.asStateFlow()

    private val _popularCommands = MutableStateFlow<List<VoiceCommandItem>>(
        listOf(
            VoiceCommandItem(
                id = "p1",
                category = CommandCategory.MUSIC,
                commandText = "YouTube par Honey Singh ka gaana chalao",
                translatedHint = "Play Honey Singh song on YouTube",
                iconType = "youtube",
                actionType = ActionType.YOUTUBE_SEARCH
            ),
            VoiceCommandItem(
                id = "p2",
                category = CommandCategory.APPS,
                commandText = "Chrome kholo",
                translatedHint = "Open Google Chrome browser",
                iconType = "chrome",
                actionType = ActionType.OPEN_APP
            ),
            VoiceCommandItem(
                id = "p3",
                category = CommandCategory.APPS,
                commandText = "WhatsApp kholo",
                translatedHint = "Open WhatsApp messenger",
                iconType = "whatsapp",
                actionType = ActionType.OPEN_APP
            ),
            VoiceCommandItem(
                id = "p4",
                category = CommandCategory.APPS,
                commandText = "Instagram kholo",
                translatedHint = "Open Instagram app",
                iconType = "instagram",
                actionType = ActionType.OPEN_APP
            ),
            VoiceCommandItem(
                id = "p5",
                category = CommandCategory.DAILY,
                commandText = "Kal subah 6 baje alarm lagao",
                translatedHint = "Set alarm for 6:00 AM tomorrow",
                iconType = "alarm",
                actionType = ActionType.SET_ALARM
            ),
            VoiceCommandItem(
                id = "p6",
                category = CommandCategory.SYSTEM,
                commandText = "Calculator kholo",
                translatedHint = "Open Calculator tool",
                iconType = "calculator",
                actionType = ActionType.OPEN_APP
            ),
            VoiceCommandItem(
                id = "p7",
                category = CommandCategory.SYSTEM,
                commandText = "Settings kholo",
                translatedHint = "Open System Settings",
                iconType = "settings",
                actionType = ActionType.OPEN_APP
            ),
            VoiceCommandItem(
                id = "p8",
                category = CommandCategory.DAILY,
                commandText = "Google par search karo ...",
                translatedHint = "Search Google on the web",
                iconType = "google",
                actionType = ActionType.WEB_SEARCH
            ),
            VoiceCommandItem(
                id = "p9",
                category = CommandCategory.SYSTEM,
                commandText = "Flashlight on karo",
                translatedHint = "Turn on camera flashlight",
                iconType = "flashlight",
                actionType = ActionType.TOGGLE_FLASHLIGHT
            )
        )
    )
    val popularCommands: StateFlow<List<VoiceCommandItem>> = _popularCommands.asStateFlow()

    private val _youtubeVideos = MutableStateFlow<List<YouTubeVideo>>(
        listOf(
            YouTubeVideo(
                id = "yt1",
                title = "Honey Singh - Millionaire (Official Video)",
                channel = "T-Series • 120M views",
                duration = "3:45",
                views = "120M views",
                videoUrl = "https://www.youtube.com/results?search_query=Honey+Singh+Millionaire"
            ),
            YouTubeVideo(
                id = "yt2",
                title = "Honey Singh - High Heels (Official Video)",
                channel = "Sony Music India • 98M views",
                duration = "4:12",
                views = "98M views",
                videoUrl = "https://www.youtube.com/results?search_query=Honey+Singh+High+Heels"
            ),
            YouTubeVideo(
                id = "yt3",
                title = "Honey Singh - Desi Kalakaar",
                channel = "Yo Yo Honey Singh • 200M views",
                duration = "4:30",
                views = "200M views",
                videoUrl = "https://www.youtube.com/results?search_query=Honey+Singh+Desi+Kalakaar"
            ),
            YouTubeVideo(
                id = "yt4",
                title = "Honey Singh - Love Dose",
                channel = "T-Series • 150M views",
                duration = "3:58",
                views = "150M views",
                videoUrl = "https://www.youtube.com/results?search_query=Honey+Singh+Love+Dose"
            )
        )
    )
    val youtubeVideos: StateFlow<List<YouTubeVideo>> = _youtubeVideos.asStateFlow()

    private val _activeYouTubeQuery = MutableStateFlow("Honey Singh - Latest Songs")
    val activeYouTubeQuery: StateFlow<String> = _activeYouTubeQuery.asStateFlow()

    private var audioVoiceEngine: AudioVoiceEngine? = null

    init {
        initAudioEngine()
    }

    private fun initAudioEngine() {
        audioVoiceEngine = AudioVoiceEngine(
            context = getApplication(),
            onTextRecognized = { text ->
                processRecognizedSpeech(text)
            },
            onErrorOccurred = { errorMsg ->
                viewModelScope.launch {
                    _assistantState.value = AssistantState.Error(errorMsg)
                }
            },
            onPartialRecognized = { partial ->
                viewModelScope.launch {
                    val curr = _assistantState.value
                    val amp = if (curr is AssistantState.Listening) curr.amplitude else 0.4f
                    _assistantState.value = AssistantState.Listening(recognizedText = partial, amplitude = amp)
                }
            }
        )

        // Observe amplitude flow
        viewModelScope.launch {
            audioVoiceEngine?.micAmplitude?.collect { amp ->
                val state = _assistantState.value
                if (state is AssistantState.Listening) {
                    _assistantState.value = state.copy(amplitude = amp)
                } else if (state is AssistantState.Speaking) {
                    _assistantState.value = state.copy(amplitude = amp)
                }
            }
        }
    }

    fun startListening() {
        _currentScreen.value = "listening"
        _assistantState.value = AssistantState.Listening(recognizedText = "", amplitude = 0.3f)
        audioVoiceEngine?.startListening(_settings.value.selectedLanguage)
    }

    fun stopListening() {
        audioVoiceEngine?.stopListening()
        val currState = _assistantState.value
        if (currState is AssistantState.Listening && currState.recognizedText.isNotBlank()) {
            processRecognizedSpeech(currState.recognizedText)
        } else {
            _assistantState.value = AssistantState.Idle
            _currentScreen.value = "home"
        }
    }

    fun stopSpeaking() {
        audioVoiceEngine?.stopSpeaking()
        _assistantState.value = AssistantState.Idle
        _currentScreen.value = "home"
    }

    fun toggleMute() {
        val curr = _assistantState.value
        if (curr is AssistantState.Speaking) {
            val newMute = !curr.isMuted
            if (newMute) {
                audioVoiceEngine?.stopSpeaking()
            }
            _assistantState.value = curr.copy(isMuted = newMute)
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun executePresetCommand(command: VoiceCommandItem) {
        processRecognizedSpeech(command.commandText)
    }

    fun searchYouTubeDirect(query: String) {
        _activeYouTubeQuery.value = query
        _currentScreen.value = "youtube"
        actionRegistry.executeYouTubeSearch(query)
    }

    private fun processRecognizedSpeech(userInput: String) {
        viewModelScope.launch {
            // Stage 1: Transition to Thinking state (matches image 2 "Thinking..." and image 1 step checklist)
            _assistantState.value = AssistantState.Thinking(query = userInput, currentStep = 0)

            // Checklist Step 0: "Understanding your request"
            delay(250)
            _assistantState.value = AssistantState.Thinking(query = userInput, currentStep = 1)

            // Step 1: "Processing with Gemini"
            val geminiResult = geminiService.processUserSpeech(userInput, _settings.value.customApiKey)

            // Step 2: "Executing action"
            _assistantState.value = AssistantState.Thinking(query = userInput, currentStep = 2)
            delay(200)

            val actionResult: ActionExecutionResult = when (geminiResult) {
                is GeminiResponseResult.ToolCall -> {
                    executeToolCall(geminiResult.functionName, geminiResult.arguments, userInput)
                }
                is GeminiResponseResult.TextResponse -> {
                    // Check if local parser finds an actionable intent
                    val localIntent = NaturalCommandParser.parse(userInput)
                    if (localIntent.actionType != ActionType.GENERAL_CONVERSATION) {
                        executeParsedIntent(localIntent, userInput)
                    } else {
                        ActionExecutionResult(
                            actionType = ActionType.GENERAL_CONVERSATION,
                            status = ActionStatus.SUCCESS,
                            userMessage = geminiResult.text,
                            voiceFeedback = geminiResult.text
                        )
                    }
                }
                is GeminiResponseResult.Failure -> {
                    // Gemini API failed or key unprovided -> Smart Offline Local Parser handles Indian commands!
                    val localIntent = NaturalCommandParser.parse(userInput)
                    executeParsedIntent(localIntent, userInput)
                }
            }

            // Step 3: "Speaking response"
            _assistantState.value = AssistantState.Thinking(query = userInput, currentStep = 3)
            delay(150)

            // Create Action Preview for the Speaking Screen
            val preview = ActionPreview(
                actionType = actionResult.actionType,
                title = actionResult.targetApp ?: "Zoya AI",
                subtitle = actionResult.userMessage,
                targetApp = actionResult.targetApp,
                query = actionResult.query,
                status = actionResult.status
            )
            _activeActionPreview.value = preview

            // Transition to Speaking Screen (matches image 1 Screen 3!)
            _currentScreen.value = "speaking"
            _assistantState.value = AssistantState.Speaking(
                speechText = actionResult.voiceFeedback,
                actionPreview = preview,
                amplitude = 0.6f
            )

            // Add to recent command history
            val newRecord = RecentCommandRecord(
                id = System.currentTimeMillis().toString(),
                prompt = userInput,
                response = actionResult.voiceFeedback,
                timeAgo = "Just now",
                status = actionResult.status,
                iconType = when (actionResult.actionType) {
                    ActionType.YOUTUBE_SEARCH -> "youtube"
                    ActionType.SET_ALARM -> "alarm"
                    ActionType.TOGGLE_FLASHLIGHT -> "flashlight"
                    ActionType.WEB_SEARCH -> "chrome"
                    ActionType.OPEN_APP -> actionResult.targetApp?.lowercase() ?: "apps"
                    else -> "mic"
                }
            )
            _recentCommands.value = listOf(newRecord) + _recentCommands.value.take(7)

            // Speak response via TTS
            audioVoiceEngine?.speak(actionResult.voiceFeedback) {
                // Speech finished callback
            }
        }
    }

    private fun executeToolCall(name: String, args: Map<String, Any?>, originalText: String): ActionExecutionResult {
        return when (name) {
            "search_youtube" -> {
                val query = args["query"]?.toString() ?: "Honey Singh"
                _activeYouTubeQuery.value = query
                actionRegistry.executeYouTubeSearch(query)
            }
            "open_supported_app" -> {
                val appName = args["app_name"]?.toString() ?: "settings"
                actionRegistry.openSupportedApp(appName)
            }
            "set_alarm" -> {
                val hour = (args["hour"] as? Number)?.toInt() ?: 6
                val minute = (args["minute"] as? Number)?.toInt() ?: 0
                val label = args["label"]?.toString() ?: "Alarm"
                actionRegistry.setAlarm(hour, minute, label)
            }
            "toggle_flashlight" -> {
                val enabled = (args["enabled"] as? Boolean) ?: true
                actionRegistry.toggleFlashlight(enabled)
            }
            "search_web" -> {
                val query = args["query"]?.toString() ?: originalText
                actionRegistry.searchWeb(query)
            }
            "get_device_time" -> {
                actionRegistry.getCurrentDeviceTime()
            }
            "stop_listening" -> {
                ActionExecutionResult(
                    actionType = ActionType.STOP_LISTENING,
                    status = ActionStatus.SUCCESS,
                    userMessage = "Stopped listening.",
                    voiceFeedback = "Listening stopped. Say 'Hey Zoya' whenever you need me."
                )
            }
            else -> {
                ActionExecutionResult(
                    actionType = ActionType.GENERAL_CONVERSATION,
                    status = ActionStatus.SUCCESS,
                    userMessage = "Completed request.",
                    voiceFeedback = "Done."
                )
            }
        }
    }

    private fun executeParsedIntent(intent: com.example.ai.ParsedIntent, originalText: String): ActionExecutionResult {
        return when (intent.actionType) {
            ActionType.YOUTUBE_SEARCH -> {
                val query = intent.query ?: "Honey Singh songs"
                _activeYouTubeQuery.value = query
                actionRegistry.executeYouTubeSearch(query)
            }
            ActionType.OPEN_APP -> {
                val app = intent.appName ?: "youtube"
                actionRegistry.openSupportedApp(app)
            }
            ActionType.SET_ALARM -> {
                actionRegistry.setAlarm(intent.hour ?: 6, intent.minute ?: 0)
            }
            ActionType.TOGGLE_FLASHLIGHT -> {
                actionRegistry.toggleFlashlight(intent.turnOnFlashlight)
            }
            ActionType.WEB_SEARCH -> {
                actionRegistry.searchWeb(intent.query ?: originalText)
            }
            ActionType.DEVICE_INFO -> {
                actionRegistry.getCurrentDeviceTime()
            }
            ActionType.WEATHER_INFO -> {
                ActionExecutionResult(
                    actionType = ActionType.WEATHER_INFO,
                    status = ActionStatus.SUCCESS,
                    userMessage = "Currently 28°C and Mostly Clear in Kolkata.",
                    voiceFeedback = "In Kolkata it is currently 28 degrees Celsius and mostly clear."
                )
            }
            ActionType.STOP_LISTENING -> {
                ActionExecutionResult(
                    actionType = ActionType.STOP_LISTENING,
                    status = ActionStatus.SUCCESS,
                    userMessage = "Listening stopped.",
                    voiceFeedback = "Listening stopped. Call me anytime!"
                )
            }
            ActionType.GENERAL_CONVERSATION -> {
                // Friendly warm conversation in Hinglish/English/Hindi
                val response = when {
                    originalText.contains("kaise ho", ignoreCase = true) || originalText.contains("kemon acho", ignoreCase = true) ->
                        "Main bilkul theek hoon! Aap batao, main aapki kya madad kar sakti hoon?"
                    originalText.contains("who are you", ignoreCase = true) || originalText.contains("kaun ho", ignoreCase = true) ->
                        "I am Zoya, your personal AI voice assistant on Android. You can ask me to play songs on YouTube, open apps, or set alarms."
                    else -> "Maine aapki baat suni: $originalText. Main aapki madad ke liye taiyaar hoon!"
                }
                ActionExecutionResult(
                    actionType = ActionType.GENERAL_CONVERSATION,
                    status = ActionStatus.SUCCESS,
                    userMessage = response,
                    voiceFeedback = response
                )
            }
        }
    }

    fun toggleWakeWord(enabled: Boolean) {
        _settings.value = _settings.value.copy(wakeWordEnabled = enabled)
        if (enabled && _settings.value.isMicPermissionGranted) {
            ZoyaWakeWordService.startService(getApplication())
        } else {
            ZoyaWakeWordService.stopService(getApplication())
        }
    }

    fun updatePermissions(micGranted: Boolean, notifGranted: Boolean) {
        _settings.value = _settings.value.copy(
            isMicPermissionGranted = micGranted,
            isNotificationPermissionGranted = notifGranted
        )
        if (_settings.value.wakeWordEnabled && micGranted) {
            ZoyaWakeWordService.startService(getApplication())
        }
    }

    fun setLanguage(lang: String) {
        _settings.value = _settings.value.copy(selectedLanguage = lang)
    }

    fun setVoice(voice: String) {
        _settings.value = _settings.value.copy(assistantVoice = voice)
    }

    fun setCustomApiKey(key: String) {
        _settings.value = _settings.value.copy(customApiKey = key)
    }

    override fun onCleared() {
        audioVoiceEngine?.destroy()
        super.onCleared()
    }
}
