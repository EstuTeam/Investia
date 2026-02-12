package com.investia.app.data.local

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.investia.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles local push notifications for alerts and trading signals.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ALERTS = "alerts_channel"
        const val CHANNEL_SIGNALS = "signals_channel"
        const val CHANNEL_GENERAL = "general_channel"
    }

    init {
        createChannels()
    }

    private fun createChannels() {
        val alertChannel = NotificationChannel(
            CHANNEL_ALERTS,
            "Fiyat Alarmları",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Fiyat alarm bildirimleri"
        }

        val signalChannel = NotificationChannel(
            CHANNEL_SIGNALS,
            "Trading Sinyalleri",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Alım/satım sinyal bildirimleri"
        }

        val generalChannel = NotificationChannel(
            CHANNEL_GENERAL,
            "Genel Bildirimler",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Genel uygulama bildirimleri"
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(alertChannel)
        manager.createNotificationChannel(signalChannel)
        manager.createNotificationChannel(generalChannel)
    }

    fun showAlertNotification(title: String, message: String, notificationId: Int = System.currentTimeMillis().toInt()) {
        showNotification(CHANNEL_ALERTS, title, message, notificationId, NotificationCompat.PRIORITY_HIGH)
    }

    fun showSignalNotification(title: String, message: String, notificationId: Int = System.currentTimeMillis().toInt()) {
        showNotification(CHANNEL_SIGNALS, title, message, notificationId, NotificationCompat.PRIORITY_DEFAULT)
    }

    fun showGeneralNotification(title: String, message: String, notificationId: Int = System.currentTimeMillis().toInt()) {
        showNotification(CHANNEL_GENERAL, title, message, notificationId, NotificationCompat.PRIORITY_LOW)
    }

    private fun showNotification(
        channelId: String,
        title: String,
        message: String,
        notificationId: Int,
        priority: Int
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
