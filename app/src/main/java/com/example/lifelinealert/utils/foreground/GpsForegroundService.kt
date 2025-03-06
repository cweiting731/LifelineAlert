package com.example.lifelinealert.utils.foreground

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.lifelinealert.R


class GpsForegroundService: Service() {

    override fun onCreate() {
        super.onCreate()

        Log.v("GpsForeground", "onCreate")
        var foregroundNotification = NotificationCompat.Builder(this, "foreground_channel")
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Gps Tracking")
            .setContentText("正在取得 Gps info")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        startForeground(2, foregroundNotification.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        /* TODO: ("Not yet implemented")
        *   在這裡請求 user location / per period 用 onBind 傳回 Activity (or MapPage?) */
        return null
    }
}