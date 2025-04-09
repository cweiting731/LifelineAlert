package com.example.lifelinealert.data

import com.example.lifelinealert.utils.foreground.Location
import com.example.lifelinealert.utils.foreground.WebSocket


object PublicDataHolder {
    var websocket: WebSocket? = null
    var location: Location? = null
}