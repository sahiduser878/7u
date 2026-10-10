package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ZoyaApplication : Application() {

    companion object {
        const val WAKE_WORD_CHANNEL_ID = "zoya_wake_word_channel"
        const val ASSISTANT_NOTIFICATION_ID = 1001
        lateinit var instance: ZoyaApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                WAKE_WORD_CHANNEL_ID,
                "Zoya AI Background Wake Word",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifies when Zoya AI is actively listening for 'Hey Zoya' wake word"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
