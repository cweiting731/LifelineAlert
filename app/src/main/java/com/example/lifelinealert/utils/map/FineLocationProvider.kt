package com.example.lifelinealert.utils.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object FineLocationProvider {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 200)
        .setMinUpdateIntervalMillis(100)
        .build()
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // code
            locationResult ?: return
        }
    }
    var isLocationRequesting = false
        private set
    private var fusedLocationProvider: FusedLocationProviderClient? = null


    fun requestLocationUpdates(activity: Activity) {
        if(fusedLocationProvider != null) {
            throw Exception("Location updates has already requested")
        }
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(activity)
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw Exception("ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions missing")
        }
        fusedLocationProvider!!.requestLocationUpdates(locationRequest, locationCallback, null)
        isLocationRequesting = true
    }

    fun removeLocationUpdates() {
        if(fusedLocationProvider == null) {
            throw Exception("No updates need to remove")
        }
        fusedLocationProvider?.removeLocationUpdates(locationCallback)
        fusedLocationProvider = null
        isLocationRequesting = false
    }
}