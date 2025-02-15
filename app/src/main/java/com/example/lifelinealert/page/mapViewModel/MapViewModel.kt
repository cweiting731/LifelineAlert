package com.example.lifelinealert.page.mapViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifelinealert.utils.map.Directions.fetchRoute
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    // Map UI state
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // default start point
    private val nckuLibrary = LatLng(22.999973101427155, 120.21985214463398)

    init {
        startTargetFetchingSimulating()
    }

    // 救護車測試資料模擬
    private fun startTargetFetchingSimulating() {
        viewModelScope.launch {
            while (true) {
                val updatedLocations = _uiState.value.locations.toMutableMap().apply {
                    put("A", LatLng(22.999973101427155 + Math.random() * 0.001, 120.2198 + Math.random() * 0.001))
                    put("B", LatLng(22.999973101427155 + Math.random() * 0.001, 120.2208 + Math.random() * 0.001))
                    put("C", LatLng(22.999973101427155 + Math.random() * 0.001, 120.2218 + Math.random() * 0.001))
                }
                // update state
                _uiState.value = _uiState.value.copy(locations = updatedLocations)
                fetchPathsForTargets(updatedLocations)

                delay(2000) // 每 2 秒更新一次位置
            }
        }
    }

    private fun fetchPathsForTargets(targetLocations: Map<String, LatLng>) {
        targetLocations.forEach { (id, location) ->
            viewModelScope.launch {
                val path = fetchRoute(location, nckuLibrary)

                // update state
                val updatedPaths = _uiState.value.polylinePaths.toMutableMap().apply {
                    put(id, path)
                }
                _uiState.value = _uiState.value.copy(polylinePaths = updatedPaths)
            }
        }
    }
}