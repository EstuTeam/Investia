package com.investia.app.data.remote

import com.investia.app.data.local.TokenManager
import kotlinx.coroutines.*
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WebSocket service for real-time market data.
 * Uses coroutines for reconnection with exponential backoff.
 */
@Singleton
class WebSocketService @Inject constructor(
    private val client: OkHttpClient,
    private val tokenManager: TokenManager
) {
    private var webSocket: WebSocket? = null
    private var isConnected = false
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var reconnectJob: Job? = null

    var onPriceUpdate: ((symbol: String, price: Double, change: Double) -> Unit)? = null
    var onSignalUpdate: ((symbol: String, signal: String) -> Unit)? = null
    var onAlertTriggered: ((alertId: String, message: String) -> Unit)? = null
    var onConnectionStateChange: ((connected: Boolean) -> Unit)? = null

    private val wsBaseUrl: String
        get() = "wss://trading-botu.vercel.app/ws"

    fun connect() {
        if (isConnected) return

        val token = tokenManager.cachedToken ?: ""

        val request = Request.Builder()
            .url(wsBaseUrl)
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                reconnectAttempts = 0
                onConnectionStateChange?.invoke(true)

                subscribe("market_overview")
                subscribe("bist30_prices")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    val type = json.optString("type", "")

                    when (type) {
                        "price_update" -> {
                            val symbol = json.optString("symbol", "")
                            val price = json.optDouble("price", 0.0)
                            val change = json.optDouble("change_percent", 0.0)
                            onPriceUpdate?.invoke(symbol, price, change)
                        }
                        "signal_update" -> {
                            val symbol = json.optString("symbol", "")
                            val signal = json.optString("signal", "")
                            onSignalUpdate?.invoke(symbol, signal)
                        }
                        "alert_triggered" -> {
                            val alertId = json.optString("alert_id", "")
                            val message = json.optString("message", "")
                            onAlertTriggered?.invoke(alertId, message)
                        }
                    }
                } catch (_: Exception) {
                    // Ignore parse errors
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // Binary messages not used
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                isConnected = false
                onConnectionStateChange?.invoke(false)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                onConnectionStateChange?.invoke(false)
                scheduleReconnect()
            }
        })
    }

    /**
     * Exponential backoff reconnection using coroutines (not raw threads).
     * Max 5 attempts: 2s, 4s, 8s, 16s, 32s
     */
    private fun scheduleReconnect() {
        if (reconnectAttempts >= maxReconnectAttempts) return

        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            reconnectAttempts++
            val delayMs = (2000L * (1L shl (reconnectAttempts - 1))).coerceAtMost(32_000L)
            delay(delayMs)
            if (!isConnected) {
                connect()
            }
        }
    }

    fun subscribe(channel: String) {
        val msg = JSONObject().apply {
            put("action", "subscribe")
            put("channel", channel)
        }
        webSocket?.send(msg.toString())
    }

    fun unsubscribe(channel: String) {
        val msg = JSONObject().apply {
            put("action", "unsubscribe")
            put("channel", channel)
        }
        webSocket?.send(msg.toString())
    }

    fun disconnect() {
        reconnectJob?.cancel()
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        isConnected = false
        reconnectAttempts = 0
    }

    fun isConnected() = isConnected

    fun destroy() {
        disconnect()
        scope.cancel()
    }
}
