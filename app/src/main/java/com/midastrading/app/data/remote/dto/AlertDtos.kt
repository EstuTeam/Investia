package com.midastrading.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.midastrading.app.domain.model.*

// ===== Alerts =====
data class AlertsResponseDto(
    val alerts: List<AlertDto> = emptyList(),
    val count: Int = 0
)

data class AlertDto(
    val id: String = "",
    val symbol: String = "",
    val type: String = "",
    @SerializedName("target_price") val targetPrice: Double = 0.0,
    val active: Boolean = true
)

data class CreateAlertDto(
    val symbol: String,
    val type: String,
    @SerializedName("target_price") val targetPrice: Double
)

data class AlertResponseDto(
    val success: Boolean = false,
    @SerializedName("alert_id") val alertId: String = "",
    val message: String = ""
)

data class AlertStatsDto(
    @SerializedName("total_alerts") val totalAlerts: Int = 0,
    @SerializedName("active_alerts") val activeAlerts: Int = 0,
    @SerializedName("triggered_today") val triggeredToday: Int = 0
)

data class ToggleAlertDto(
    val success: Boolean = false,
    val message: String = ""
)

// ===== Notifications =====
data class NotificationItemDto(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val priority: String = "medium",
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("created_at") val createdAt: String = ""
) {
    fun toDomain() = NotificationItem(id, title, message, type, priority, isRead, createdAt)
}

data class NotificationsDto(
    val notifications: List<NotificationItemDto> = emptyList(),
    val count: Int = 0
)
