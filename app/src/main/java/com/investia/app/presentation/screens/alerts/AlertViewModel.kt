package com.investia.app.presentation.screens.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.domain.model.AlertItem
import com.investia.app.domain.model.NotificationItem
import com.investia.app.domain.repository.AlertRepository
import com.investia.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val alerts: List<AlertItem> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val selectedTab: Int = 0, // 0=Alerts, 1=Notifications
    val showCreateDialog: Boolean = false
)

@HiltViewModel
class AlertViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AlertState())
    val state: StateFlow<AlertState> = _state.asStateFlow()

    init {
        loadAlerts()
    }

    fun loadAlerts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = alertRepository.getActiveAlerts()
            _state.value = _state.value.copy(
                isLoading = false,
                alerts = (result as? Resource.Success)?.data ?: emptyList(),
                error = (result as? Resource.Error)?.message
            )
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = alertRepository.getNotificationHistory()
            _state.value = _state.value.copy(
                isLoading = false,
                notifications = (result as? Resource.Success)?.data ?: emptyList()
            )
        }
    }

    fun selectTab(tab: Int) {
        _state.value = _state.value.copy(selectedTab = tab)
        if (tab == 1) loadNotifications()
        else loadAlerts()
    }

    fun createAlert(symbol: String, type: String, targetPrice: Double) {
        viewModelScope.launch {
            alertRepository.createAlert(symbol, type, targetPrice)
            _state.value = _state.value.copy(showCreateDialog = false)
            loadAlerts()
        }
    }

    fun toggleAlert(id: String) {
        viewModelScope.launch {
            alertRepository.toggleAlert(id)
            loadAlerts()
        }
    }

    fun deleteAlert(id: String) {
        viewModelScope.launch {
            alertRepository.deleteAlert(id)
            loadAlerts()
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            alertRepository.markNotificationRead(id)
            loadNotifications()
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            alertRepository.markAllRead()
            loadNotifications()
        }
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = false)
    }
}
