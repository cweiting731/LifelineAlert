package com.example.lifelinealert.utils.foreground

interface WebsocketCallBack {
    fun onSuccess()
    fun onFailure(error: String?)
}