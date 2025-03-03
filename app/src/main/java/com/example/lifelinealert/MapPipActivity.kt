package com.example.lifelinealert

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MapPipActivity : ComponentActivity() {
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.v("pip", "onUserLeaveHint")
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            Toast.makeText(this, "Device not support PIP mode !", Toast.LENGTH_SHORT).show()
            Log.v("pip", "not support")
        } else {
            val aspectRatio = Rational(16, 9)
            val pipBuilder = PictureInPictureParams.Builder().setAspectRatio(aspectRatio)
            enterPictureInPictureMode(pipBuilder.build())
            Log.v("pip", "pip mode")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("pip", "onCreate")
        setContent {
            PipPage()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PipPage() {
        Box() {
            Row {
                Image(imageVector = Icons.Default.Face, contentDescription = "test")
                Text(text = "Hello World")
            }
        }
    }
}