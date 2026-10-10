package com.example.ai

import com.example.model.ActionType
import java.util.Locale
import java.util.regex.Pattern

data class ParsedIntent(
    val actionType: ActionType,
    val query: String? = null,
    val appName: String? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val turnOnFlashlight: Boolean = true,
    val confidence: Float = 0.95f
)

object NaturalCommandParser {

    fun parse(rawInput: String): ParsedIntent {
        val text = rawInput.trim().lowercase(Locale.ROOT)

        // 1. Stop Listening
        if (text.contains("stop listening") ||
            text.contains("chup raho") ||
            text.contains("band karo") ||
            text.contains("chup thako") ||
            text.contains("thamo")
        ) {
            return ParsedIntent(actionType = ActionType.STOP_LISTENING)
        }

        // 2. YouTube Search & Playback
        // Examples:
        // "YouTube par Honey Singh ka gaana chalao"
        // "YouTube e Honey Singh er gaan bajao"
        // "Play Arijit Singh on YouTube"
        // "YouTube par search karo ..."
        val isYouTubeCommand = text.contains("youtube") || text.contains("gaana") || text.contains("gaan") || text.contains("song")
        if (isYouTubeCommand) {
            // Check if just opening YouTube
            if (text == "youtube" || text == "youtube kholo" || text == "open youtube" || text == "youtube open karo" || text == "youtube chalao") {
                return ParsedIntent(actionType = ActionType.OPEN_APP, appName = "youtube")
            }

            var query = extractYouTubeQuery(text)
            if (query.isNotBlank()) {
                return ParsedIntent(actionType = ActionType.YOUTUBE_SEARCH, query = query)
            }
        }

        // 3. Set Alarm
        // "Kal subah 6 baje alarm lagao", "Set alarm for 6 AM", "6 baje alarm lagao", "Sokal 6 tay alarm dao"
        val alarmIntent = extractAlarmDetails(text)
        if (alarmIntent != null) {
            return alarmIntent
        }

        // 4. Flashlight / Torch
        if (text.contains("flashlight") || text.contains("torch") || text.contains("flash light")) {
            val turnOff = text.contains("off") || text.contains("band") || text.contains("nibhao") || text.contains("bujhao")
            return ParsedIntent(
                actionType = ActionType.TOGGLE_FLASHLIGHT,
                turnOnFlashlight = !turnOff
            )
        }

        // 5. Open Apps
        val appName = extractAppName(text)
        if (appName != null) {
            return ParsedIntent(actionType = ActionType.OPEN_APP, appName = appName)
        }

        // 6. Web Search
        // "Google par search karo ...", "Search ... on Google", "Google e search koro ..."
        if (text.contains("search karo") || text.contains("search koro") || text.contains("google par") || text.contains("search on google")) {
            val query = extractWebSearchQuery(text)
            if (query.isNotBlank()) {
                return ParsedIntent(actionType = ActionType.WEB_SEARCH, query = query)
            }
        }

        // 7. Time / Date queries
        if (text.contains("time") || text.contains("samay") || text.contains("samoy") || text.contains("baje") || text.contains("kotota baje")) {
            return ParsedIntent(actionType = ActionType.DEVICE_INFO)
        }

        // 8. Weather queries
        if (text.contains("weather") || text.contains("mausam") || text.contains("hawa")) {
            return ParsedIntent(actionType = ActionType.WEATHER_INFO, query = "current location")
        }

        // General Conversation fallback
        return ParsedIntent(
            actionType = ActionType.GENERAL_CONVERSATION,
            query = rawInput
        )
    }

    private fun extractYouTubeQuery(input: String): String {
        var clean = input

        // Clean out carrier phrases in Hindi, Bengali, English
        val prefixes = listOf(
            "youtube par", "youtube mein", "youtube me", "youtube pe", "youtube e",
            "play on youtube", "search on youtube", "on youtube", "play", "chalao",
            "bajao", "search karo", "search koro"
        )
        for (prefix in prefixes) {
            clean = clean.replace(prefix, "")
        }

        // Clean suffixes
        val suffixes = listOf(
            "ka gaana chalao", "ka gana chalao", "ke gaane chalao", "er gaan bajao",
            "er gaan chalao", "ka gaana", "ka gana", "er gaan", "er song", "ka song",
            "chalao", "bajao", "lagao", "play karo", "search karo", "kholo"
        )
        for (suffix in suffixes) {
            clean = clean.replace(suffix, "")
        }

        clean = clean.replace("youtube", "").trim()
        if (clean.isBlank()) {
            return "Latest trending songs"
        }
        return clean
    }

    private fun extractAlarmDetails(text: String): ParsedIntent? {
        if (!text.contains("alarm") && !text.contains("jagao") && !text.contains("uthana")) {
            return null
        }

        // Extract hour and minute
        val timeRegex = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?\\s*(baje|am|pm|ta|tay)?", Pattern.CASE_INSENSITIVE)
        val matcher = timeRegex.matcher(text)

        var hour = 6
        var minute = 0

        if (matcher.find()) {
            val hStr = matcher.group(1)
            val mStr = matcher.group(2)
            if (hStr != null) {
                hour = hStr.toIntOrNull() ?: 6
            }
            if (mStr != null) {
                minute = mStr.toIntOrNull() ?: 0
            }

            // Subah / Shaam / PM check
            val isEvening = text.contains("shaam") || text.contains("shyam") || text.contains("raat") || text.contains("pm")
            if (isEvening && hour < 12) {
                hour += 12
            }
            return ParsedIntent(
                actionType = ActionType.SET_ALARM,
                hour = hour,
                minute = minute
            )
        }

        // Default to 6:00 AM if no time mentioned but alarm requested
        return ParsedIntent(
            actionType = ActionType.SET_ALARM,
            hour = 6,
            minute = 0
        )
    }

    private fun extractAppName(text: String): String? {
        val openWords = listOf("kholo", "open", "khulun", "chalao", "launch", "start", "koro")
        val hasOpenIntent = openWords.any { text.contains(it) }

        val apps = listOf(
            "whatsapp", "instagram", "chrome", "calculator", "settings",
            "youtube", "camera"
        )

        for (app in apps) {
            if (text.contains(app)) {
                // If it's pure app command or has open intent
                if (hasOpenIntent || text.split(" ").size <= 2) {
                    return app
                }
            }
        }
        return null
    }

    private fun extractWebSearchQuery(text: String): String {
        var clean = text
            .replace("google par search karo", "")
            .replace("google e search koro", "")
            .replace("google search karo", "")
            .replace("search on google", "")
            .replace("search google for", "")
            .replace("google par", "")
            .replace("google me", "")
            .replace("search karo", "")
            .replace("search koro", "")
            .replace("google", "")
            .trim()
        return clean.ifBlank { "Google AI" }
    }
}
