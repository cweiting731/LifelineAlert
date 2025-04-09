package com.example.lifelinealert.utils.foreground

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocket(private val serverUrl: String) {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var webSocketCallBack : WebsocketCallBack? = null

    fun connect() {
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.v("WebSocket", "onOpen")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.v("WebSocket", "onClosing ${code} ${reason}")
                webSocket.close(1000, null) // 可能導致連線不完全關閉，影響資源釋放
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                webSocketCallBack?.onFailure("${response}")
            }
        })
    }

    fun connectWebSocketCallBack(websocketCallBack: WebsocketCallBack) {
        webSocketCallBack = websocketCallBack
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "Client closed connection")
    }
}