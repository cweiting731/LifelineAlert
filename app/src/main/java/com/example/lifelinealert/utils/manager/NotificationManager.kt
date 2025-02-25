package com.example.lifelinealert.utils.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.lifelinealert.R

object NotificationManager {
    private val emergencySoundUri : Map<String, Uri> = mapOf(
        "left" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.left_emergency}"),
        "right" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.right_emergency}")
    )

    // 創建通知通道（Android 8.0 以上需要）
    fun createNotificationChannel(context: Context, channelId: String) {
        val channel = NotificationChannel(
            channelId, // 通道 ID
            "背景通知",        // 通道名稱
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
//            val uri = Uri.Builder()
//                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//                .authority(context.packageName)
//                .appendPath(context.resources.getResourceEntryName(R.raw.left_emergency))
//                .build()
//
//            val attributes = AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .build()

            description = "這是前景服務通知頻道"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableLights(true)
            enableVibration(true)
            setShowBadge(true) // 小紅點 未讀通知
            setSound(null, null)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
//        manager?.deleteNotificationChannel(channelId)
        manager?.createNotificationChannel(channel)
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

    fun sendNotificationEmergency(context: Context, title: String, message: String, channelId: String, direction: String) {
        val soundUri: Uri = emergencySoundUri[direction] ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val uri = Uri.Builder()
//            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//            .authority(context.packageName)
//            .appendPath(context.resources.getResourceEntryName(R.raw.left_emergency))
//            .build()

        val ringtone = RingtoneManager.getRingtone(context, soundUri)
        ringtone.play()

        // 建立通知
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 讓通知顯示彈出
            .setSound(null)
            .build()

        // 確保使用 context 來取得 NotificationManager
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.notify(1, notification) // 發送通知，確保 notificationManager 不是 null
    }

//    fun sendNotificationEmergency(
//        context: Context,
//        title: String,
//        message: String,
////        mainActivity: Class<out Activity>, // 會建一個新的Activity 避免錯誤以及記憶體洩漏
//        channelId: String
//    ) {
////        val intent = Intent(context, mainActivity) // 點擊通知後跳轉到指定 Activity
////        val pendingIntent = PendingIntent.getActivity(
////            context,
////            0,
////            intent,
////            PendingIntent.FLAG_MUTABLE
////        )
//
////        val soundUri: Uri = emergencySoundUri[channelId] ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//        val notification = NotificationCompat.Builder(context, channelId)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setCategory(NotificationCompat.CATEGORY_ALARM)
//            .setPriority(NotificationCompat.PRIORITY_HIGH) // 彈出通知
////            .setFullScreenIntent(pendingIntent, true) // 設為全螢幕通知
//            .setVibrate(longArrayOf(0, 500, 1000)) // 震動設定
//            .setAutoCancel(true) // 點擊後自動取消通知
////            .setSound(soundUri)
//            .setDefaults(NotificationCompat.DEFAULT_SOUND)
//            .build()
//        // 建立通知
//
//
//        // 發送通知
////        val notificationManager =
////            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val notificationManager = context.getSystemService(NotificationManager::class.java)
//        notificationManager.notify(System.currentTimeMillis().toInt(), notification) // 發送通知
//
//        Log.v("notification", "send ${channelId} successfully")
//    }


}

