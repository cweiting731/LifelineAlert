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

    /**
     * LocationRequest is configured to request location updates with high accuracy and a small update interval.
     * - Priority.PRIORITY_HIGH_ACCURACY: Requests the most accurate location.
     * - Update interval of 200 ms, with a minimum update interval of 100 ms.
     */
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 200)
        .setMinUpdateIntervalMillis(100)
        .build()
    private val defaultLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // code
            locationResult ?: return
        }
    }
    private var locationCallback: LocationCallback = defaultLocationCallback
    var isLocationRequesting = false
        private set
    private var fusedLocationProvider: FusedLocationProviderClient? = null

    /**
     * Requests location updates with the default location callback.
     * You can only request once in the global.
     * This callback will only request for location update,
     * call another overloading function for custom your callback`
     * Ensures that required permissions (ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION) are granted.
     * @param activity The activity context used to get the FusedLocationProviderClient.
     * @throws Exception if location updates are already requested or if permissions are missing.
     */
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

    /**
     * Requests location updates with a custom location callback.
     * You can only request once in the global.
     * Ensures that required permissions (ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION) are granted.
     * @param activity The activity context used to get the FusedLocationProviderClient.
     * @param locationCallback A custom callback to handle location updates.
     * @throws Exception if location updates are already requested or if permissions are missing.
     */
    fun requestLocationUpdates(activity: Activity, locationCallback: LocationCallback) {
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
        this.locationCallback = locationCallback
        fusedLocationProvider!!.requestLocationUpdates(locationRequest, locationCallback, null)
        isLocationRequesting = true
    }

    /**
     * Removes location updates.
     * Resets the location callback to the default callback.
     * If location updates were not requested previously, an exception will be thrown.
     * @throws Exception if no location updates need to be removed.
     */
    fun removeLocationUpdates() {
        if(fusedLocationProvider == null) {
            throw Exception("No updates need to remove")
        }
        fusedLocationProvider?.removeLocationUpdates(locationCallback)
        fusedLocationProvider = null
        isLocationRequesting = false
        locationCallback = defaultLocationCallback
    }
}