package com.example.model

enum class ActionStatus {
    IDLE,
    IN_PROGRESS,
    SUCCESS,
    FAILED,
    NEEDS_PERMISSION,
    UNSUPPORTED
}

enum class ActionType {
    YOUTUBE_SEARCH,
    OPEN_APP,
    SET_ALARM,
    TOGGLE_FLASHLIGHT,
    WEB_SEARCH,
    DEVICE_INFO,
    WEATHER_INFO,
    STOP_LISTENING,
    GENERAL_CONVERSATION
}

enum class CommandCategory(val label: String) {
    ALL("All"),
    APPS("Apps"),
    MUSIC("Music"),
    DAILY("Daily"),
    SYSTEM("System")
}

data class ActionPreview(
    val actionType: ActionType,
    val title: String,
    val subtitle: String,
    val targetApp: String? = null,
    val query: String? = null,
    val status: ActionStatus = ActionStatus.IN_PROGRESS
)

sealed class AssistantState {
    object Idle : AssistantState()
    data class Listening(
        val recognizedText: String = "",
        val amplitude: Float = 0.3f
    ) : AssistantState()
    data class Thinking(
        val query: String = "",
        val currentStep: Int = 0 // 0: Understanding, 1: Processing, 2: Executing, 3: Speaking
    ) : AssistantState()
    data class Speaking(
        val speechText: String,
        val actionPreview: ActionPreview? = null,
        val amplitude: Float = 0.5f,
        val isMuted: Boolean = false
    ) : AssistantState()
    data class Error(val message: String) : AssistantState()
}

data class VoiceCommandItem(
    val id: String,
    val category: CommandCategory,
    val commandText: String,
    val translatedHint: String,
    val iconType: String,
    val actionType: ActionType
)

data class SupportedApp(
    val id: String,
    val name: String,
    val packageName: String,
    val iconType: String,
    val isAvailable: Boolean = true
)

data class YouTubeVideo(
    val id: String,
    val title: String,
    val channel: String,
    val duration: String,
    val views: String,
    val videoUrl: String,
    val thumbnailUrl: String = ""
)

data class ZoyaSettings(
    val wakeWordEnabled: Boolean = true,
    val assistantVoice: String = "Zoya (Female)",
    val selectedLanguage: String = "Auto detect (Hindi, Bengali, English, Hinglish)",
    val isMicPermissionGranted: Boolean = false,
    val isNotificationPermissionGranted: Boolean = false,
    val isAccessibilityGranted: Boolean = false,
    val themeName: String = "Glassmorphism (Pink)",
    val customApiKey: String = ""
)

data class RecentCommandRecord(
    val id: String,
    val prompt: String,
    val response: String,
    val timeAgo: String,
    val status: ActionStatus = ActionStatus.SUCCESS,
    val iconType: String = "mic"
)
