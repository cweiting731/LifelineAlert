package com.example.lifelinealert.utils.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat

object PermissionManager {
    private val REQUEST_CODE = 100

    // 處理 alert dialog 用
    private var showDialog : MutableState<Boolean> = mutableStateOf(false)
    private var alertDialogDescription : MutableState<String> = mutableStateOf("")
    private val alertDialogPermissionTitle: Map<String, String> = mapOf( // let user understand which permission need to open
        "android.permission.POST_NOTIFICATIONS" to "通知",
        "android.permission.ACCESS_FINE_LOCATION" to "位置"
    )
    fun requestPermission(activity: Activity) {
        val unGrantedPermissions = mutableListOf<String>()
        Log.v("permission", "requesting")
        // Post Notification 存取權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 以上需要存取
            if (!hasPermission(activity, Manifest.permission.POST_NOTIFICATIONS)) {
                unGrantedPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else Log.v("permission", "notification permission already on")

        // Fine Location 存取權限
        if (!hasPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            unGrantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        } else Log.v("permission", "fine location permission already on")

        if (unGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, unGrantedPermissions.toTypedArray(), REQUEST_CODE)
        }
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun handlePermissionResult(activity: Activity, permissions: Array<String>, grantResults: IntArray) {
        for (i in permissions.indices) {
            val permission = permissions[i]
            val grantResult = grantResults[i]

            // 已有permission
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                Log.v("permission", "$permission granted")
            }
            else {
                // 沒有permission 且已無重複次數
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    Log.v("permission", "$permission never ask, change to setting")
                    showDialog.value = true
                    val title = alertDialogPermissionTitle[permission]
                    alertDialogDescription.value = "Lifeline Alert 需要 $title 的權限"
                }
                // 沒有permission 但有重複次數
                else {
                    Log.v("permission", "$permission denied, still can ask")
                }
            }
        }
    }

    private fun showPermissionSettingDialog(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    @Composable
    fun AlertDialog(activity: Activity) {
        if (showDialog.value) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    Button(onClick = {
                        showDialog.value = false
                        showPermissionSettingDialog(activity)
                    }) {
                        Text(text = "確定")
                    }
                },
                title = { Text(text = "權限存取警告") },
                text = { Text(text = alertDialogDescription.value) }
            )
        }
    }
}