package com.example.lifelinealert

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.lifelinealert.data.PublicDataHolder
import com.example.lifelinealert.utils.foreground.GpsForegroundService
import com.example.lifelinealert.utils.foreground.WebSocket
import com.example.lifelinealert.utils.manager.PermissionManager


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainActivityViewModel
//    private val right_channelId = "right_emergency"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

    // request permission
        PublicDataHolder.websocket = WebSocket(this)
        if(PermissionManager.requestPermissions(this)) { // 代表所有權限都已開啟
                startGpsForegroundService()
        }


        setContent {
            MainPage(viewModel)
        }
    }

    private fun startGpsForegroundService() {
        if (!isGpsServiceRunning()) {
            val gpsForegroundService = Intent(this, GpsForegroundService::class.java)
            startForegroundService(gpsForegroundService)
        }
    }
    private fun isGpsServiceRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (GpsForegroundService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            PermissionManager.handlePermissionResult(this, permissions, grantResults)

            var judge = true
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    judge = false
                }
            }
            if (judge) {
                startGpsForegroundService()
            }
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
        if (PermissionManager.requestPermissions(this)) {
            startGpsForegroundService()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.v("lowerSystem", "stop")

//        Handler(Looper.getMainLooper()).postDelayed({
//            Log.v("lowerSystem", "notification testing")
//            NotificationManager.sendNotification(
//                this,
//                "警告",
//                "前方路口有救護車從右側出沒！請減速！",
//                right_channelId,
//                PRIORITY_HIGH,
//                action = { MediaManager.MediaPlay(this, RIGHT) }
//            )
//        }, 5000)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

//        if (viewModel.pageRoute.value == "map") {
//            val aspectRatio = Rational(40, 40)
//            val pipBuilder = PictureInPictureParams.Builder().setAspectRatio(aspectRatio)
//            enterPictureInPictureMode(pipBuilder.build())
//            Log.v("pip", "pip mode")
//        }
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
