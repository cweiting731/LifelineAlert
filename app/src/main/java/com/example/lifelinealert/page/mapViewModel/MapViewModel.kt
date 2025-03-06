package com.example.lifelinealert.page.mapViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifelinealert.utils.map.Directions.fetchRoute
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel() {

    // Map UI state
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // default start point
    private val nckuLibrary = LatLng(22.999973101427155, 120.21985214463398)

    val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if(!_uiState.value.allowCameraTracing) return
            Log.d("locationCallback", "tracking")
            val location = locationResult.lastLocation
            location ?: return
            val curLocation = LatLng(location.latitude, location.longitude)
            val curBearing = location.bearing
            _uiState.value = _uiState.value.copy(userCameraPosition = CameraPosition.Builder()
                .target(curLocation)
                .zoom(17f)
                .bearing(curBearing)
                .build()
            )
        }
    }

    init {
        startTargetFetchingSimulating()
    }

    // 救護車測試資料模擬
    private fun startTargetFetchingSimulating() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val updatedLocations = _uiState.value.locations.toMutableMap().apply {
                    put("A", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2198 + (Math.random()-0.5) * 0.02))
                    put("B", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2208 + (Math.random()-0.5) * 0.02))
                    put("C", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2218 + (Math.random()-0.5) * 0.02))
                    put("D", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2218 + (Math.random()-0.5) * 0.02))
                    put("E", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2218 + (Math.random()-0.5) * 0.02))
                    put("F", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2218 + (Math.random()-0.5) * 0.02))
                    put("G", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2218 + (Math.random()-0.5) * 0.02))
                    put("H", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2218 + (Math.random()-0.5) * 0.02))
                    put("I", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2218 + (Math.random()-0.5) * 0.02))
                    put("J", LatLng(22.999973101427155 + (Math.random()-0.5) * 0.02, 120.2218 + (Math.random()-0.5) * 0.02))
                }
//                Log.d("ThreadInfo", "目前執行緒: ${Thread.currentThread().name}")
                withContext(Dispatchers.Main) {
                    // update state
                    fetchPathsForTargets(updatedLocations)
                    _uiState.value = _uiState.value.copy(locations = updatedLocations)
                    Log.d("startTargetFetchingSimulating", "目前執行緒: ${Thread.currentThread().name}, 更新location")
                }
                delay(10000) // 每 10 秒更新一次位置
            }
        }
    }

    private suspend fun fetchPathsForTargets(targetLocations: Map<String, LatLng>) {
            val updatedPaths = targetLocations.mapValues { (_, location) ->
                fetchRoute(location, nckuLibrary)
            }

            _uiState.value = _uiState.value.copy(polylinePaths = updatedPaths)
            Log.d("startTargetFetchingSimulating", "目前執行緒: ${Thread.currentThread().name}, 更新path")
    }

    fun myLocationButtonClick(): Boolean {
        _uiState.value = _uiState.value.copy(allowCameraTracing = !_uiState.value.allowCameraTracing)
        return false
    }

    fun mapDrag() {
        Log.v("onMapDrag", "map is dragged")
        if(_uiState.value.allowCameraTracing){
            _uiState.value = _uiState.value.copy(allowCameraTracing = false)
        }
    }
}