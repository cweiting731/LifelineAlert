package com.example.lifelinealert.utils.permissions;

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.Activity

object PermissionManager {
    // add needed permission here
    private val permissions = listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.POST_NOTIFICATIONS,
    )

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: Activity, requestCode: Int) {
        val ungrantedPermissions = permissions.filter { permission ->
            !hasPermission(activity, permission)
        }
        if (ungrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, ungrantedPermissions.toTypedArray(), requestCode)
        }
    }
}
