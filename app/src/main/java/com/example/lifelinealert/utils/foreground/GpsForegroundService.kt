package com.example.lifelinealert.utils.foreground

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.lifelinealert.R
import com.example.lifelinealert.utils.manager.SnackbarManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.util.LinkedList
import java.util.Queue

data class Location(val latitude: Double, val longitude: Double)

class GpsForegroundService: Service(), WebsocketCallBack {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val webSocket = WebSocket("ws://192.168.209.98:8080", this)
    private val userName = "Willy"

    private var locations: Queue<Location> = LinkedList()
    private val capacity: Int = 5
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        webSocket.connect()

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

                    if (locations.size >= capacity) locations.poll()
                    locations.offer(Location(location.latitude, location.longitude))

                    uploadLocationToServer()
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
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(10, notification)
    }

    private fun resizeBitmap(resourceId: Int, width: Int, height: Int, context: Context): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        return resizedBitmap
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 15000  // 每 5 秒獲取一次 GPS
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun uploadLocationToServer() {
        val userData = packagingUserGpsData()
        val jsonUserData = Gson().toJson(userData)
        webSocket.sendMessage(jsonUserData)
    }

    private fun packagingUserGpsData() : Map<String, Any> {
        val locationMap = locations.mapIndexed { index, location ->
            index.toString() to location
        }.toMap()

        val userData = mapOf(
            "lastUpdateTime" to System.currentTimeMillis(),
            "location" to locationMap
        )

        return userData
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.v("GpsForegroundService", "onDestroy")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        webSocket.close()
    }


    // connect failure callback
    override fun onSuccess() {
        Log.v("WebSocket", "Connection successful in Service")
        // 在這裡處理成功的情況，比如更新 UI 或執行其他操作
    }

    override fun onFailure(error: String?) {
        Log.e("WebSocket", "Connection failed in Service: $error")
        SnackbarManager.showMessage(
            "與伺服器連接失敗!",
            "重新連接",
            {
//                SnackbarManager.showMessage("連接中~")
                webSocket.connect()
            },
            {

            }
        )
    }
}