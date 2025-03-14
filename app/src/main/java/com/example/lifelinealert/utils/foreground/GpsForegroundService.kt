package com.example.lifelinealert.utils.foreground

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.lifelinealert.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class GpsForegroundService: Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val channel = NotificationChannel(
            "GpsForegroundService",
            "GPS Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.v("LocationService", "經度: ${location.longitude}, 緯度: ${location.latitude}")
                }
            }
        }

        startForegroundService()
        requestLocationUpdates()
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "GpsForegroundService")
            .setContentTitle("GPS 追蹤中")
            .setContentText("應用正在獲取 GPS 位置")
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000  // 每 5 秒獲取一次 GPS
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun uploadLocationToServer(lat: Double, lng: Double) {
        // 這裡可以用 Retrofit、OkHttp 或 Firebase 來上傳位置
        Log.d("Upload", "上傳到後端: ($lat, $lng)")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.v("GpsForegroundService", "onDestroy")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}