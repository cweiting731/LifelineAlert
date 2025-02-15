package com.example.lifelinealert.foreground

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.lifelinealert.R

class NotificationManager {
    // 請求通知權限
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
            else {
                Log.v("lowerSystem", "notification permission already on")
            }
        }
    }

    // 創建通知通道（Android 8.0 以上需要）
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "foreground_channel", // 通道 ID
                "背景通知",        // 通道名稱
                NotificationManager.IMPORTANCE_HIGH // 設為 HIGH 讓通知跳出
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel) // 確保不為 null
        }
    }

    // 發送通知
    fun sendNotification(context: Context, title: String, message: String) {
        // 建立通知
        val notification = NotificationCompat.Builder(context, "foreground_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 讓通知顯示彈出
            .build()

        // 確保使用 context 來取得 NotificationManager
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.notify(1, notification) // 發送通知，確保 notificationManager 不是 null
    }

}

