package com.example.lifelinealert

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.lifelinealert.utils.manager.IMPORTANCE_HIGH
import com.example.lifelinealert.utils.manager.MediaManager
import com.example.lifelinealert.utils.manager.NotificationManager
import com.example.lifelinealert.utils.manager.PRIORITY_HIGH
import com.example.lifelinealert.utils.manager.PermissionManager
import com.example.lifelinealert.utils.manager.RIGHT


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private val right_channelId = "right_emergency"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // request permission
        PermissionManager.requestPermissions(this)

        // create notification channel
        NotificationManager.createNotificationChannel(
            context = this,
            name = "foreground",
            channelId = "foreground",
            importance = IMPORTANCE_HIGH,
            descriptionText = "foreground",
            soundUri = null,
            audioAttributes = null
        )

        setContent {
            MainPage(viewModel)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            PermissionManager.handlePermissionResult(this, permissions, grantResults)
        }
    }

    override fun onPause() {


        super.onPause()
        Log.v("lowerSystem", "pause")
//        sendNotification(this)
//        showDialog.value = true

    }

    override fun onResume() {
        super.onResume()
        Log.v("lowerSystem", "resume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.v("lowerSystem", "restart")
        PermissionManager.requestPermissions(this)
    }

    override fun onStop() {
        super.onStop()
        Log.v("lowerSystem", "stop")

        Handler(Looper.getMainLooper()).postDelayed({
//            notificationManager.sendNotification(
//                this,
//                "警告",
//                "前方十字路口有救護車從右側出沒！請減速！"
//            )
            Log.v("lowerSystem", "notification testing")
            NotificationManager.sendNotification(
                this,
                "警告",
                "前方路口有救護車從右側出沒！請減速！",
//                MainActivity::class.java,
                right_channelId,
                PRIORITY_HIGH,
                action = { MediaManager.MediaPlay(this, RIGHT) }
            )
//            notificationManager.sendNotification(this, "test", "test")
        }, 5000)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (viewModel.pageRoute.value == "map") {
            val aspectRatio = Rational(40, 40)
            val pipBuilder = PictureInPictureParams.Builder().setAspectRatio(aspectRatio)
            enterPictureInPictureMode(pipBuilder.build())
            Log.v("pip", "pip mode")
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (isInPictureInPictureMode) {
            viewModel.setShowController(false)
        }
        else {
            viewModel.setShowController(true)
        }
    }
}
