package com.example.lifelinealert.utils.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import androidx.annotation.IntDef
import androidx.core.app.NotificationCompat
import com.example.lifelinealert.R

const val IMPORTANCE_NONE = android.app.NotificationManager.IMPORTANCE_NONE
const val IMPORTANCE_MIN = android.app.NotificationManager.IMPORTANCE_MIN
const val IMPORTANCE_LOW = android.app.NotificationManager.IMPORTANCE_LOW
const val IMPORTANCE_DEFAULT = android.app.NotificationManager.IMPORTANCE_DEFAULT
const val IMPORTANCE_HIGH = android.app.NotificationManager.IMPORTANCE_HIGH

const val LOCKSCREEN_VISIBILITY_PRIVATE = NotificationCompat.VISIBILITY_PRIVATE
const val LOCKSCREEN_VISIBILITY_PUBLIC = NotificationCompat.VISIBILITY_PUBLIC
const val LOCKSCREEN_VISIBILITY_SECRET = NotificationCompat.VISIBILITY_SECRET

object NotificationManager {
    private val emergencySoundUri : Map<String, Uri> = mapOf(
        "left" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.left_emergency}"),
        "right" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.right_emergency}"),
        "back" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.back_emergency}"),
        "front" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.front_emergency}")
    )

    @IntDef(IMPORTANCE_NONE, IMPORTANCE_MIN, IMPORTANCE_LOW, IMPORTANCE_DEFAULT, IMPORTANCE_HIGH)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NotificationImportance

    @IntDef(LOCKSCREEN_VISIBILITY_SECRET, LOCKSCREEN_VISIBILITY_PUBLIC, LOCKSCREEN_VISIBILITY_PRIVATE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NotificationLockscreenVisibility

    // 創建通知通道（Android 8.0 以上需要）
    fun createNotificationChannel(
        context: Context,
        name: String,
        channelId: String,
        @NotificationImportance importance: Int = IMPORTANCE_DEFAULT,
        descriptionText: String,
        @NotificationLockscreenVisibility lockscreenVisibilityId: Int = LOCKSCREEN_VISIBILITY_PUBLIC,
        light: Boolean = false,
        vibration: Boolean = true,
        showBadge: Boolean = true,
        soundUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
        audioAttributes: AudioAttributes? = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
    ) {
        val channel = NotificationChannel(
            channelId, // 通道 ID
            name,      // 通道名稱
            importance, // 重要程度
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

            description = descriptionText
            lockscreenVisibility = lockscreenVisibilityId
            enableLights(light)
            enableVibration(vibration)
            setShowBadge(showBadge) // 小紅點 未讀通知
            setSound(soundUri, audioAttributes)
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


}

