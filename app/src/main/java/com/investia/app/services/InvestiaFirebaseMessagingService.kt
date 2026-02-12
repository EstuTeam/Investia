package com.investia.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.investia.app.R
import com.investia.app.presentation.MainActivity

class InvestiaFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "InvestiaFCM"
        private const val CHANNEL_ID_ALERTS = "investia_alerts"
        private const val CHANNEL_ID_GENERAL = "investia_general"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        sendTokenToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received from: ${message.from}")

        // Data payload
        val data = message.data
        val type = data["type"] ?: "general"
        val title = data["title"] ?: message.notification?.title ?: "Investia"
        val body = data["body"] ?: message.notification?.body ?: ""
        val ticker = data["ticker"]

        when (type) {
            "price_alert" -> showAlertNotification(title, body, ticker)
            "portfolio_update" -> showNotification(CHANNEL_ID_ALERTS, title, body)
            "news" -> showNotification(CHANNEL_ID_GENERAL, title, body)
            else -> showNotification(CHANNEL_ID_GENERAL, title, body)
        }
    }

    private fun showAlertNotification(title: String, body: String, ticker: String?) {
        showNotification(
            channelId = CHANNEL_ID_ALERTS,
            title = title,
            body = if (ticker != null) "[$ticker] $body" else body,
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    private fun showNotification(
        channelId: String,
        title: String,
        body: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        createNotificationChannels()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val alertsChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Fiyat Alarmları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Fiyat hedefi ve portföy alarmları"
                enableVibration(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "Genel Bildirimler",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Haberler ve genel bildirimler"
            }

            manager.createNotificationChannel(alertsChannel)
            manager.createNotificationChannel(generalChannel)
        }
    }

    private fun sendTokenToServer(token: String) {
        // TODO: Backend'e FCM token gönder
        // RetrofitClient.apiService.updateFcmToken(token)
        Log.d(TAG, "Token should be sent to backend: $token")
    }
}
