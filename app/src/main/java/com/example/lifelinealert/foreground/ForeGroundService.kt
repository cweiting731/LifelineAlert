package com.example.lifelinealert.foreground

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.lifelinealert.R


class ForeGroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("lowerSystem", "Starting ForeGround Service")

        val notification = NotificationCompat.Builder(this, "foreground_channel")
            .setContentTitle("後台運行")
            .setContentText("你的 App 正在後台運行")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        sendNotification(this, "ForeGroundService", "Successfully")
        Log.d("lowerSystem", "ForeGround successfully")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}