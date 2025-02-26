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
            Log.v("location", "tracking")
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
//                    put("A", LatLng(22.999973101427155 + Math.random() * 0.001, 120.2198 + Math.random() * 0.001))
//                    put("B", LatLng(22.999973101427155 + Math.random() * 0.001, 120.2208 + Math.random() * 0.001))
//                    put("C", LatLng(22.999973101427155 + Math.random() * 0.001, 120.2218 + Math.random() * 0.001))
                }
//                Log.d("ThreadInfo", "目前執行緒: ${Thread.currentThread().name}")
                withContext(Dispatchers.Main) {
                    // update state
                    _uiState.value = _uiState.value.copy(locations = updatedLocations)
                    fetchPathsForTargets(updatedLocations)
                }
                delay(2000) // 每 2 秒更新一次位置
            }
        }
    }

    private fun fetchPathsForTargets(targetLocations: Map<String, LatLng>) {
        viewModelScope.launch {
            val updatedPaths = targetLocations.mapValues { (_, location) ->
                fetchRoute(location, nckuLibrary)
            }

            _uiState.value = _uiState.value.copy(polylinePaths = updatedPaths)
        }
    }

    fun myLocationButtonClick(): Boolean {
        _uiState.value = _uiState.value.copy(allowCameraTracing = !_uiState.value.allowCameraTracing)
        return false
    }

    fun mapDrag(): Unit{
        Log.v("onMapDrag", "map is dragged")
        if(_uiState.value.allowCameraTracing){
            _uiState.value = _uiState.value.copy(allowCameraTracing = false)
        }
    }
}