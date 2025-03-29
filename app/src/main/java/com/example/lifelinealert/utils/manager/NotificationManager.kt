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

const val PRIORITY_MAX = NotificationCompat.PRIORITY_MAX
const val PRIORITY_HIGH = NotificationCompat.PRIORITY_HIGH
const val PRIORITY_DEFAULT = NotificationCompat.PRIORITY_DEFAULT
const val PRIORITY_LOW = NotificationCompat.PRIORITY_LOW
const val PRIORITY_MIN = NotificationCompat.PRIORITY_MIN

object NotificationManager {

    // 限制createNotificationChannel只能傳入特定的值
    @IntDef(IMPORTANCE_NONE, IMPORTANCE_MIN, IMPORTANCE_LOW, IMPORTANCE_DEFAULT, IMPORTANCE_HIGH)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NotificationImportance

    @IntDef(LOCKSCREEN_VISIBILITY_SECRET, LOCKSCREEN_VISIBILITY_PUBLIC, LOCKSCREEN_VISIBILITY_PRIVATE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NotificationLockscreenVisibility

    @IntDef(PRIORITY_MAX, PRIORITY_HIGH, PRIORITY_DEFAULT, PRIORITY_LOW, PRIORITY_MIN)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NotificationPriority

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
            description = descriptionText
            lockscreenVisibility = lockscreenVisibilityId
            enableLights(light)
            enableVibration(vibration)
            setShowBadge(showBadge) // 小紅點 未讀通知
            setSound(soundUri, audioAttributes)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    // 發送通知
    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String,
        @NotificationPriority priority: Int = PRIORITY_DEFAULT,
        action: (() -> Unit)? = null
    ) {
        // 建立通知
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(priority)
            .build()

        action?.invoke()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.notify(1, notification)
    }
}

