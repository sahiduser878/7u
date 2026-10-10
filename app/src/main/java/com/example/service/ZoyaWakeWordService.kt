package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.ZoyaApplication
import com.example.wakeword.WakeWordDetector

class ZoyaWakeWordService : Service() {

    companion object {
        const val ACTION_START = "com.example.action.START_WAKE_WORD"
        const val ACTION_STOP = "com.example.action.STOP_WAKE_WORD"
        const val ACTION_WAKE_WORD_TRIGGERED = "com.example.action.WAKE_WORD_TRIGGERED"

        fun startService(context: Context) {
            val intent = Intent(context, ZoyaWakeWordService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, ZoyaWakeWordService::class.java).apply {
                action = ACTION_STOP
            }
            context.stopService(intent)
        }
    }

    private var wakeWordDetector: WakeWordDetector? = null

    override fun onCreate() {
        super.onCreate()
        wakeWordDetector = WakeWordDetector(this) {
            onWakeWordDetected()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForegroundService()
            return START_NOT_STICKY
        }

        val notification = createForegroundNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                ZoyaApplication.ASSISTANT_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(ZoyaApplication.ASSISTANT_NOTIFICATION_ID, notification)
        }

        wakeWordDetector?.startListening()
        return START_STICKY
    }

    private fun onWakeWordDetected() {
        // Send broadcast to update UI if active
        val broadcastIntent = Intent(ACTION_WAKE_WORD_TRIGGERED).apply {
            setPackage(packageName)
        }
        sendBroadcast(broadcastIntent)

        // Bring MainActivity to the front
        val activityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_START_LISTENING", true)
        }
        startActivity(activityIntent)
    }

    private fun createForegroundNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, ZoyaWakeWordService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, ZoyaApplication.WAKE_WORD_CHANNEL_ID)
            .setContentTitle("Zoya AI Voice Assistant")
            .setContentText("Listening for 'Hey Zoya' in background")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .build()
    }

    private fun stopForegroundService() {
        wakeWordDetector?.stopListening()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        wakeWordDetector?.stopListening()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
