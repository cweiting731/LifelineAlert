package com.example.lifelinealert.page.mapViewModel

import com.example.lifelinealert.utils.foreground.NotificationManager
import com.google.android.gms.maps.model.LatLng

data class MapUiState(
    val nckuLibrary: LatLng = LatLng(22.999973101427155, 120.21985214463398), // original
    val locations: Map<String, LatLng> = emptyMap(),
    val polylinePaths: Map<String, List<LatLng>> = emptyMap(),
    val notificationManager: NotificationManager = NotificationManager(),
)