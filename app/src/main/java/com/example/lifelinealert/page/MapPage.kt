package com.example.lifelinealert.page

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifelinealert.R
import com.example.lifelinealert.page.mapViewModel.MapViewModel
import com.example.lifelinealert.utils.map.FineLocationPermissionHandler
import com.example.lifelinealert.utils.map.FineLocationProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapPage(mapViewModel: MapViewModel = viewModel()) {
    // 座標相關資料
    val mapUiState by mapViewModel.uiState.collectAsState()
    val nckuLibrary = mapUiState.nckuLibrary
    val targetLocations = mapUiState.locations
    val polylinePaths = mapUiState.polylinePaths
    val userCameraPosition = mapUiState.userCameraPosition
    val cameraPositionState = rememberCameraPositionState { position = userCameraPosition }
    // 權限相關
    val fineLocationPermissionHandler = FineLocationPermissionHandler()
    val context = LocalContext.current
    // UI values
    val bottomBarHeight = 80.dp // default NavigationBarHeight is 80.dp
    val fineLocationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)  // 跳轉觸發重組

    LaunchedEffect(Unit) {
        Log.v("MapPage", "compose create")
    }
    MapAnimateEffect(cameraPositionState, mapViewModel)
    UserLocationUpdateEffect(mapViewModel) // userLocation register
    MapOnDragEffect(cameraPositionState, mapViewModel)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomBarHeight)
    ) {
        if (fineLocationPermissionHandler.isGranted(context) || fineLocationPermissionState.status.isGranted) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true), // 啟用當前位置
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true, // 啟用定位按鈕
                    zoomControlsEnabled = false,
                ),
                onMyLocationButtonClick = {
                    mapViewModel.myLocationButtonClick()
                },
            ) {
                Marker(
                    state = MarkerState(position = nckuLibrary),
                    title = "library",
                    snippet = "國立成功大學圖書館",
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
                    polylinePaths[id]?.let { path ->
                        Polyline(
                            points = path,
                            color = Color.Red,
//                            color = colorList.random(),
                            width = 8f
                        )
                        Marker(
                            state = MarkerState(position = location),
                            icon = resizeBitmap(R.drawable.ambulance_icon, 100, 100, context)
                        )
                    }
                }
            }
        } else {
            Button(
                onClick = {
                    fineLocationPermissionHandler.requestPermissionFromSettings(context)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
            ) {
                Text("需要精準位置權限授權，點擊跳轉設定")
            }
        }
    }
}

@Composable
fun MapAnimateEffect(
    cameraPositionState: CameraPositionState,
    mapViewModel: MapViewModel = viewModel()
) {
    val mapState by mapViewModel.uiState.collectAsState()
    val userCameraPosition = mapState.userCameraPosition
    val allowCameraTracing = mapState.allowCameraTracing
    val fineLocationPermissionHandler = FineLocationPermissionHandler()
    val context = LocalContext.current

    // map myLocation animate
    LaunchedEffect(userCameraPosition) {
        if (!fineLocationPermissionHandler.isGranted(context) ||
            !allowCameraTracing
        ) {
            return@LaunchedEffect
        }
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(userCameraPosition),
            durationMs = 700
        )
        Log.d("MapAnimateEffect", "$userCameraPosition")
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun UserLocationUpdateEffect(
    mapViewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val fineLocationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)  // 跳轉觸發重組
    val locationCallback = mapViewModel.locationCallback

    // userLocation register
    DisposableEffect(context, fineLocationPermissionState) {
        if (context is Activity) {
            Log.d("UserLocationUpdateEffect", "start LocationUpdates")
            try {
                FineLocationProvider.requestLocationUpdates(context, locationCallback)
            } catch (e: Exception) {
                Log.e("UserLocationUpdateEffect", e.toString())
            }
        }
        onDispose {
            Log.v("FineLocationProvider", "end LocationUpdates")
            Log.v("FineLocationProvider", "compose dispose")
            FineLocationProvider.removeLocationUpdates()
        }
    }
}

@Composable
fun MapOnDragEffect(
    cameraPositionState: CameraPositionState,
    mapViewModel: MapViewModel = viewModel()
) {
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            mapViewModel.mapDrag()
        }
    }
}

fun resizeBitmap(resourceId: Int, width: Int, height: Int, context: Context): BitmapDescriptor {
    val imageBitmap = BitmapFactory.decodeResource(context.resources, resourceId)
    val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
}