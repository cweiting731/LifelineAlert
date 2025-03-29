package com.example.lifelinealert.utils.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import com.example.lifelinealert.R

const val LEFT = "left"
const val RIGHT = "right"
const val BACK = "back"
const val FRONT = "front"

const val MUSIC = AudioAttributes.CONTENT_TYPE_MUSIC
const val MOVIE = AudioAttributes.CONTENT_TYPE_MOVIE
const val SONIFICATION = AudioAttributes.CONTENT_TYPE_SONIFICATION

const val NOTIFICATION = AudioAttributes.USAGE_NOTIFICATION
const val NOTIFICATION_RINGTONE = AudioAttributes.USAGE_NOTIFICATION_RINGTONE
const val MEDIA = AudioAttributes.USAGE_MEDIA

object MediaManager {
    private val mediaList : Map<String, Uri> = mapOf(
        "left" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.left_emergency}"),
        "right" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.right_emergency}"),
        "back" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.back_emergency}"),
        "front" to Uri.parse("android.resource://com.example.lifelinealert/${R.raw.front_emergency}")
    )

    @StringDef(LEFT, RIGHT, BACK, FRONT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class MediaSelected

    @IntDef(MUSIC, MOVIE, SONIFICATION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ContentType

    @IntDef(NOTIFICATION, NOTIFICATION_RINGTONE, MEDIA)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Usage

    fun MediaPlay(
        context: Context,
        @MediaSelected tag: String,
        @ContentType contentType: Int = SONIFICATION,
        @Usage usage: Int = NOTIFICATION
    ) {
        val uri = mediaList[tag]?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // 取得當前的媒體音量
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        // 設定降低的音量
        val reducedVolume = (maxVolume * 0.1).toInt()

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, reducedVolume, 0) // flag 音量調整的額外操作
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(contentType)
                    .setUsage(usage)
                    .build()
            )
            setDataSource(context, uri)
            prepare()
            start()

            // 播放結束後恢復音量
            setOnCompletionListener {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
                release()
            }

            // 發生錯誤恢復音量
            setOnErrorListener { mp, what, extra ->
                Log.e("MediaPlayer", "${extra}")
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
                release()
                true
            }
        }
    }
}