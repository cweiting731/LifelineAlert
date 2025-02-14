package com.example.lifelinealert.utils.map

import android.util.Log
import com.example.lifelinealert.BuildConfig
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*

object Directions {
    private val client = OkHttpClient()
    private const val API_KEY = BuildConfig.DIRECTIONS_API_KEY

    suspend fun fetchRoute(start: LatLng, destination: LatLng): List<LatLng> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=${start.latitude},${start.longitude}" +
                        "&destination=${destination.latitude},${destination.longitude}" +
                        "&mode=driving" + // driving / bicycling / transit
                        "&key=$API_KEY"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val json = response.body?.string()
                if (json.isNullOrEmpty()) {
                    throw IllegalArgumentException("Response body is null or empty")
                }
                PathJsonParser.parseRoute(json) // 呼叫 JsonParser 解析 JSON
            } catch (e: Exception) {
                Log.e("FetchRouteError", "Error fetching route: ${e.message}", e)
                emptyList()
            }
        }
    }
}