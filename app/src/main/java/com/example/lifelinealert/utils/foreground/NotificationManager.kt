package com.example.lifelinealert.utils.foreground

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.lifelinealert.R

class NotificationManager {

    // 請求通知權限
    @Deprecated("Use 'PermissionManager' instead", level = DeprecationLevel.ERROR)
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
                NotificationManager.IMPORTANCE_HIGH,  // 設為 HIGH 讓通知跳出
            ).apply {
                description = "這是前景服務通知頻道"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
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

    fun sendNotificationHigh(context: Context, title: String, message: String, mainActivity: Class<out Activity>) {
        val intent = Intent(context, mainActivity) // 點擊通知後跳轉到指定 Activity
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE// 新版需指定 FLAG_IMMUTABLE 或 FLAG_MUTABLE
        )

        // 建立通知
        val notification = NotificationCompat.Builder(context, "foreground_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 彈出通知
            .setFullScreenIntent(pendingIntent, true) // 設為全螢幕通知
            .setVibrate(longArrayOf(0, 500, 1000)) // 震動設定
            .setDefaults(NotificationCompat.DEFAULT_SOUND) // 預設音效
            .setLights(0xFF00FF00.toInt(), 500, 500) // 設定燈光閃爍 (綠色)
            .setAutoCancel(true) // 點擊後自動取消通知
            .build()

        // 發送通知
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification) // 發送通知
    }


}

