package com.example.lifelinealert.page

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifelinealert.page.mapViewModel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapPage(mapViewModel: MapViewModel = viewModel()) {
    // 座標相關資料
    val mapUiState by mapViewModel.uiState.collectAsState()
    val nckuLibrary = mapUiState.nckuLibrary
    val targetLocations = mapUiState.locations
    val polylinePaths = mapUiState.polylinePaths
    // 權限相關
    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nckuLibrary, 15f)
    }
    val context = LocalContext.current
    // UI values
    val bottomBarHeight = 80.dp // default NavigationBarHeight is 80.dp

    LaunchedEffect(cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomBarHeight)
    ) {
        if (cameraPermissionState.status.isGranted) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true), // 啟用當前位置
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true // 啟用定位按鈕
                )
            ) {
                Marker(
                    state = MarkerState(position = nckuLibrary),
                    title = "library",
                    snippet = "國立成功大學圖書館"
                )
                val colorList: List<Color> = listOf(
                    Color.Red,
                    Color.Green,
                    Color.Blue,
                    Color.Yellow,
                    Color.Cyan,
                    Color.Magenta
                )
                targetLocations.forEach { (id, location) ->
                    Marker(
                        state = MarkerState(position = location)
                    )
                    polylinePaths[id]?.let { path ->
                        Polyline(
                            points = path,
                            color = colorList.random(),
                            width = 8f
                        )
                    }
                }
            }
        } else {
            Button(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", "com.example.lifelinealert", null)
                    })
                },
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("需要精準位置權限授權，點擊跳轉設定")
            }
        }
    }
}