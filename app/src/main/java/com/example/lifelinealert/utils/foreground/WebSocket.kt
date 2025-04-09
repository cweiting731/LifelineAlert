package com.example.lifelinealert.utils.foreground

import android.content.Context
import android.util.Log
import com.example.lifelinealert.data.PublicDataHolder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class WebSocket(private val context: Context) {

    private lateinit var client : OkHttpClient
    private var webSocket: WebSocket? = null
    private var webSocketCallBack : WebsocketCallBack? = null
    private lateinit var serverUrl: String
    // "ws://192.168.71.198:8765"
    init {
        // 設置 OkHttpClient，並啟用雙向 SSL 驗證
//        client = getOkHttpClientWithMutualAuthentication(context)
//        client = getOkHttpClientWithoutAuthenticationTest()
    }
    fun connect() {
        client = OkHttpClient()
        serverUrl = PublicDataHolder.serverIP
        if (serverUrl == "") {
            Log.v("WebSocket", "Don't have serverUrl")
            webSocketCallBack?.onFailure("serverURL is null")
            return
        }
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
                webSocketCallBack?.onFailure("${t}\n${response}")
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

    fun loadCertificate(context: Context, certificateName: String): X509Certificate {
        val inputStream = context.assets.open(certificateName)
        val certificateFactory = CertificateFactory.getInstance("X.509")
        return certificateFactory.generateCertificate(inputStream) as X509Certificate
    }

    fun createKeyStore(context: Context): KeyStore {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
        }

        // 加載 client 憑證
        val clientCert = loadCertificate(context, "client-cert.pem")
        keyStore.setCertificateEntry("client", clientCert)

        // 加載 server 憑證
        val serverCert = loadCertificate(context, "server-cert.pem")
        keyStore.setCertificateEntry("server", serverCert)

        return keyStore
    }

    fun getTrustManagerFactory(keyStore: KeyStore): TrustManagerFactory {
        // 設定 TrustManager (用來驗證 server 憑證)
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        return trustManagerFactory
    }

    fun getKeyManagerFactory(keyStore: KeyStore): KeyManagerFactory {
        // 設定 KeyManager (用來提供 client 憑證)
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, null)
        return keyManagerFactory
    }

    fun getSSLContextWithMutualAuthentication(keyStore: KeyStore, trustManagerFactory: TrustManagerFactory, keyManagerFactory: KeyManagerFactory): SSLContext {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)
        return sslContext
    }

    fun getOkHttpClientWithMutualAuthentication(context: Context): OkHttpClient {
        val keyStore = createKeyStore(context)
        val trustManagerFactory = getTrustManagerFactory(keyStore)
        val keyManagerFactory = getKeyManagerFactory(keyStore)

        val sslContext = getSSLContextWithMutualAuthentication(keyStore, trustManagerFactory, keyManagerFactory)

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManagerFactory.trustManagers[0] as X509TrustManager)
            .build()
    }

    fun getOkHttpClientWithoutAuthenticationTest() : OkHttpClient {
        // 自訂 SSLContext，忽略憑證驗證
        val sslContext = SSLContext.getInstance("TLS")
        val trustAllCertificates = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            override fun checkClientTrusted(certs: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(certs: Array<out X509Certificate>?, authType: String?) {}
        }

        // 初始化 SSLContext，不進行憑證驗證
        sslContext.init(null, arrayOf<TrustManager>(trustAllCertificates), java.security.SecureRandom())

        val sslSocketFactory = sslContext.socketFactory
        val trustManager = trustAllCertificates

        // 設定 OkHttpClient
        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager) // 使用自訂的 SSL 設定
            .hostnameVerifier { _, _ -> true } // 忽略主機名驗證
            .build()
    }

}