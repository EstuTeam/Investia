package com.midastrading.app.data.repository

import com.midastrading.app.data.local.CacheManager
import com.midastrading.app.data.remote.MidasApiService
import com.midastrading.app.data.remote.dto.CreateAlertDto
import com.midastrading.app.domain.model.*
import com.midastrading.app.domain.repository.AlertRepository
import com.midastrading.app.util.Resource
import com.midastrading.app.util.safeApiCall
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val api: MidasApiService,
    private val cache: CacheManager
) : AlertRepository {

    override suspend fun getActiveAlerts(): Resource<List<AlertItem>> {
        cache.get<List<AlertItem>>(CacheManager.KEY_ALERTS)?.let { return Resource.Success(it) }
        return safeApiCall {
            api.getActiveAlerts().alerts.map { dto ->
                AlertItem(
                    id = dto.id, symbol = dto.symbol, type = dto.type,
                    targetPrice = dto.targetPrice, active = dto.active
                )
            }
        }.also { result ->
            if (result is Resource.Success) result.data?.let { cache.put(CacheManager.KEY_ALERTS, it, CacheManager.TTL_ALERTS) }
        }
    }

    override suspend fun createAlert(symbol: String, type: String, targetPrice: Double): Resource<Boolean> {
        cache.invalidate(CacheManager.KEY_ALERTS)
        return safeApiCall { api.createAlert(CreateAlertDto(symbol, type, targetPrice)).success }
    }

    override suspend fun deleteAlert(id: String): Resource<Boolean> {
        cache.invalidate(CacheManager.KEY_ALERTS)
        return safeApiCall { api.deleteAlert(id).success }
    }

    override suspend fun toggleAlert(id: String): Resource<Boolean> {
        cache.invalidate(CacheManager.KEY_ALERTS)
        return safeApiCall { api.toggleAlert(id).success }
    }

    override suspend fun checkTriggeredAlerts(): Resource<List<AlertItem>> {
        return safeApiCall {
            api.checkTriggeredAlerts().alerts.map { dto ->
                AlertItem(
                    id = dto.id, symbol = dto.symbol, type = dto.type,
                    targetPrice = dto.targetPrice, active = dto.active, triggered = true
                )
            }
        }
    }

    override suspend fun getNotificationHistory(): Resource<List<NotificationItem>> {
        return safeApiCall {
            api.getNotificationHistory().notifications.map { it.toDomain() }
        }
    }

    override suspend fun markNotificationRead(id: String): Resource<Boolean> {
        return safeApiCall { api.markNotificationRead(id).success }
    }

    override suspend fun markAllRead(): Resource<Boolean> {
        return safeApiCall { api.markAllNotificationsRead().success }
    }
}
