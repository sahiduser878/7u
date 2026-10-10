package com.example.actions

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import com.example.model.ActionStatus
import com.example.model.ActionType
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ActionExecutionResult(
    val actionType: ActionType,
    val status: ActionStatus,
    val userMessage: String,
    val voiceFeedback: String,
    val targetApp: String? = null,
    val query: String? = null
)

class ActionRegistry(private val context: Context) {

    private val supportedApps = mapOf(
        "youtube" to listOf("com.google.android.youtube"),
        "whatsapp" to listOf("com.whatsapp", "com.whatsapp.w4b"),
        "instagram" to listOf("com.instagram.android"),
        "chrome" to listOf("com.android.chrome"),
        "calculator" to listOf(
            "com.google.android.calculator",
            "com.android.calculator2",
            "com.sec.android.app.popupcalculator",
            "com.miui.calculator"
        ),
        "settings" to listOf("com.android.settings"),
        "camera" to listOf("android.media.action.IMAGE_CAPTURE"),
        "alarm" to listOf("com.google.android.deskclock", "com.sec.android.app.clockpackage")
    )

    private var isTorchOn = false

    fun executeYouTubeSearch(query: String): ActionExecutionResult {
        return try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val ytAppIntent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage("com.google.android.youtube")
                putExtra("query", query)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            val launched = if (isIntentAvailable(ytAppIntent)) {
                context.startActivity(ytAppIntent)
                true
            } else {
                // Fallback to web browser search for YouTube
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/results?search_query=$encoded")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
                false
            }

            val feedback = if (launched) {
                "Opening YouTube and searching for $query."
            } else {
                "YouTube app is not installed. Opening YouTube search in your browser."
            }

            ActionExecutionResult(
                actionType = ActionType.YOUTUBE_SEARCH,
                status = ActionStatus.SUCCESS,
                userMessage = "Searching YouTube for: $query",
                voiceFeedback = feedback,
                targetApp = "YouTube",
                query = query
            )
        } catch (e: Exception) {
            ActionExecutionResult(
                actionType = ActionType.YOUTUBE_SEARCH,
                status = ActionStatus.FAILED,
                userMessage = "Failed to launch YouTube: ${e.localizedMessage}",
                voiceFeedback = "Sorry, I could not open YouTube.",
                targetApp = "YouTube",
                query = query
            )
        }
    }

    fun openSupportedApp(appNameRaw: String): ActionExecutionResult {
        val appName = appNameRaw.lowercase(Locale.ROOT).trim()

        // Handle direct system shortcuts
        when (appName) {
            "settings" -> {
                return try {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    ActionExecutionResult(
                        actionType = ActionType.OPEN_APP,
                        status = ActionStatus.SUCCESS,
                        userMessage = "Opened Settings",
                        voiceFeedback = "Opening Settings for you.",
                        targetApp = "Settings"
                    )
                } catch (e: Exception) {
                    ActionExecutionResult(
                        actionType = ActionType.OPEN_APP,
                        status = ActionStatus.FAILED,
                        userMessage = "Could not open Settings",
                        voiceFeedback = "Unable to open Settings.",
                        targetApp = "Settings"
                    )
                }
            }
            "camera" -> {
                return try {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    ActionExecutionResult(
                        actionType = ActionType.OPEN_APP,
                        status = ActionStatus.SUCCESS,
                        userMessage = "Opened Camera",
                        voiceFeedback = "Opening Camera.",
                        targetApp = "Camera"
                    )
                } catch (e: Exception) {
                    ActionExecutionResult(
                        actionType = ActionType.OPEN_APP,
                        status = ActionStatus.FAILED,
                        userMessage = "Could not open Camera",
                        voiceFeedback = "Could not launch camera.",
                        targetApp = "Camera"
                    )
                }
            }
        }

        val packageCandidates = supportedApps[appName]
        if (packageCandidates == null) {
            return ActionExecutionResult(
                actionType = ActionType.OPEN_APP,
                status = ActionStatus.UNSUPPORTED,
                userMessage = "App '$appNameRaw' is not in the allowlist",
                voiceFeedback = "Sorry, $appNameRaw is not in my supported applications list.",
                targetApp = appNameRaw
            )
        }

        for (pkg in packageCandidates) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                return try {
                    context.startActivity(launchIntent)
                    ActionExecutionResult(
                        actionType = ActionType.OPEN_APP,
                        status = ActionStatus.SUCCESS,
                        userMessage = "Opened $appNameRaw",
                        voiceFeedback = "Opening $appNameRaw.",
                        targetApp = appNameRaw
                    )
                } catch (e: Exception) {
                    ActionExecutionResult(
                        actionType = ActionType.OPEN_APP,
                        status = ActionStatus.FAILED,
                        userMessage = "Failed to launch $appNameRaw",
                        voiceFeedback = "Unable to open $appNameRaw.",
                        targetApp = appNameRaw
                    )
                }
            }
        }

        return ActionExecutionResult(
            actionType = ActionType.OPEN_APP,
            status = ActionStatus.FAILED,
            userMessage = "$appNameRaw is not installed on this device",
            voiceFeedback = "$appNameRaw does not appear to be installed on your device.",
            targetApp = appNameRaw
        )
    }

    fun setAlarm(hour: Int, minute: Int, message: String = "Alarm"): ActionExecutionResult {
        return try {
            val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (isIntentAvailable(alarmIntent)) {
                context.startActivity(alarmIntent)
                val amPm = if (hour >= 12) "PM" else "AM"
                val displayHour = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }
                val formattedTime = String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
                ActionExecutionResult(
                    actionType = ActionType.SET_ALARM,
                    status = ActionStatus.SUCCESS,
                    userMessage = "Alarm set for $formattedTime",
                    voiceFeedback = "Alarm set for $formattedTime.",
                    targetApp = "Alarm"
                )
            } else {
                ActionExecutionResult(
                    actionType = ActionType.SET_ALARM,
                    status = ActionStatus.UNSUPPORTED,
                    userMessage = "No compatible Clock app found",
                    voiceFeedback = "I couldn't find a clock application to set an alarm.",
                    targetApp = "Alarm"
                )
            }
        } catch (e: Exception) {
            ActionExecutionResult(
                actionType = ActionType.SET_ALARM,
                status = ActionStatus.FAILED,
                userMessage = "Error setting alarm: ${e.localizedMessage}",
                voiceFeedback = "Sorry, I encountered an error while setting the alarm.",
                targetApp = "Alarm"
            )
        }
    }

    fun toggleFlashlight(turnOn: Boolean): ActionExecutionResult {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            if (cameraManager == null) {
                return ActionExecutionResult(
                    actionType = ActionType.TOGGLE_FLASHLIGHT,
                    status = ActionStatus.UNSUPPORTED,
                    userMessage = "Camera service unavailable",
                    voiceFeedback = "Flashlight service is not available on this device."
                )
            }

            val cameraIdList = cameraManager.cameraIdList
            var flashCameraId: String? = null
            for (id in cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                if (hasFlash) {
                    flashCameraId = id
                    break
                }
            }

            if (flashCameraId != null) {
                cameraManager.setTorchMode(flashCameraId, turnOn)
                isTorchOn = turnOn
                val actionWord = if (turnOn) "turned on" else "turned off"
                ActionExecutionResult(
                    actionType = ActionType.TOGGLE_FLASHLIGHT,
                    status = ActionStatus.SUCCESS,
                    userMessage = "Flashlight $actionWord",
                    voiceFeedback = "Flashlight is now $actionWord."
                )
            } else {
                ActionExecutionResult(
                    actionType = ActionType.TOGGLE_FLASHLIGHT,
                    status = ActionStatus.UNSUPPORTED,
                    userMessage = "Device has no flashlight",
                    voiceFeedback = "This device does not appear to have a camera flashlight."
                )
            }
        } catch (e: Exception) {
            ActionExecutionResult(
                actionType = ActionType.TOGGLE_FLASHLIGHT,
                status = ActionStatus.FAILED,
                userMessage = "Flashlight error: ${e.localizedMessage}",
                voiceFeedback = "I could not toggle the flashlight."
            )
        }
    }

    fun searchWeb(query: String): ActionExecutionResult {
        return try {
            val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, query)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (isIntentAvailable(searchIntent)) {
                context.startActivity(searchIntent)
            } else {
                val encoded = URLEncoder.encode(query, "UTF-8")
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/search?q=$encoded")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(browserIntent)
            }
            ActionExecutionResult(
                actionType = ActionType.WEB_SEARCH,
                status = ActionStatus.SUCCESS,
                userMessage = "Searching Google for: $query",
                voiceFeedback = "Searching Google for $query.",
                query = query
            )
        } catch (e: Exception) {
            ActionExecutionResult(
                actionType = ActionType.WEB_SEARCH,
                status = ActionStatus.FAILED,
                userMessage = "Web search failed",
                voiceFeedback = "Could not perform web search."
            )
        }
    }

    fun getCurrentDeviceTime(): ActionExecutionResult {
        val sdf = SimpleDateFormat("h:mm a, EEEE, d MMMM", Locale.getDefault())
        val formatted = sdf.format(Date())
        return ActionExecutionResult(
            actionType = ActionType.DEVICE_INFO,
            status = ActionStatus.SUCCESS,
            userMessage = "Current Time: $formatted",
            voiceFeedback = "It is currently $formatted."
        )
    }

    private fun isIntentAvailable(intent: Intent): Boolean {
        val activities = context.packageManager.queryIntentActivities(intent, 0)
        return activities.isNotEmpty()
    }
}
