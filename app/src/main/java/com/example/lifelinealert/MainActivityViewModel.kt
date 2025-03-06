package com.example.lifelinealert

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    var showController = mutableStateOf(true)
        private set
    var pageRoute = mutableStateOf("map")
        private set

    fun setShowController(value: Boolean) {
        this.showController.value = value
    }

    fun setPageRoute(value: String) {
        pageRoute.value = value
    }
}