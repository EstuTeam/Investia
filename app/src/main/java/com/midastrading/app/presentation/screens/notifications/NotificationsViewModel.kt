package com.midastrading.app.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midastrading.app.domain.model.NotificationItem
import com.midastrading.app.domain.repository.AlertRepository
import com.midastrading.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsState(
    val isLoading: Boolean = true,
    val notifications: List<NotificationItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = alertRepository.getNotificationHistory()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notifications = result.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            alertRepository.markNotificationRead(id)
            _state.value = _state.value.copy(
                notifications = _state.value.notifications.map {
                    if (it.id == id) it.copy(isRead = true) else it
                }
            )
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            alertRepository.markAllRead()
            _state.value = _state.value.copy(
                notifications = _state.value.notifications.map { it.copy(isRead = true) }
            )
        }
    }
}
