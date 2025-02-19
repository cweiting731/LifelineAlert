package com.example.lifelinealert.utils.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class FineLocationPermissionHandler {

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    /**
     * Checks whether the ACCESS_FINE_LOCATION permission has been granted.
     *
     * @param context The current application context.
     * @return True if the permission has been granted, otherwise false.
     */
    fun isGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests the ACCESS_FINE_LOCATION permission from the user.
     *
     * This method should be called within an Activity, otherwise a ClassCastException will be thrown.
     *
     * @param context The current Activity context.
     * @throws ClassCastException If the context is not an instance of Activity.
     */
    @Deprecated("Use 'PermissionManager' instead", level = DeprecationLevel.ERROR)
    fun requestPermission(context: Context) {
        if (context !is Activity) {
            throw ClassCastException("context should be a instance of Activity")
        }
        val activity = context as Activity
        if (isGranted(activity)) return
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Redirects the user to the application's settings page to manually grant location permissions.
     *
     * This method should be called within an Activity, otherwise a ClassCastException will be thrown.
     *
     * @param context The current Activity context.
     * @throws ClassCastException If the context is not an instance of Activity.
     */
    fun requestPermissionFromSettings(context: Context) {
        if (context !is Activity) {
            throw ClassCastException("context should be a instance of Activity")
        }
        val activity = context as Activity
        if (isGranted(activity)) return
        activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", "com.example.lifelinealert", null)
        })
    }
}
