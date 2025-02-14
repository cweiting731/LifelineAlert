package com.example.lifelinealert.utils.map

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

object PathJsonParser {
    fun parseRoute(json: String): List<LatLng> {
        val routePoints = mutableListOf<LatLng>()
        val jsonObject = JSONObject(json)

        val routesArray = jsonObject.optJSONArray("routes") ?: return routePoints
        if (routesArray.length() > 0) {
            val route = routesArray.getJSONObject(0)
            val legs = route.optJSONArray("legs") ?: return routePoints

            if (legs.length() > 0) {
                val steps = legs.getJSONObject(0).optJSONArray("steps") ?: return routePoints

                for (i in 0 until steps.length()) {
                    val step = steps.getJSONObject(i)
                    val startLoc = step.getJSONObject("start_location")
                    val endLoc = step.getJSONObject("end_location")

                    routePoints.add(LatLng(startLoc.getDouble("lat"), startLoc.getDouble("lng")))
                    routePoints.add(LatLng(endLoc.getDouble("lat"), endLoc.getDouble("lng")))
                }
            }
        }
        return routePoints
    }
}
