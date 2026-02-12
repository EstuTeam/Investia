package com.investia.app.presentation.screens.dailypicks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.domain.model.StockPick
import com.investia.app.domain.repository.MarketRepository
import com.investia.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DailyPicksState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val picks: List<StockPick> = emptyList(),
    val strategy: String = "hybrid",
    val isRefreshing: Boolean = false,
    val timestamp: String = ""
)

@HiltViewModel
class DailyPicksViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyPicksState())
    val state: StateFlow<DailyPicksState> = _state.asStateFlow()

    init {
        loadPicks()
    }

    fun loadPicks() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = marketRepository.getDailyPicks(_state.value.strategy)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        picks = result.data?.picks ?: emptyList(),
                        timestamp = result.data?.timestamp ?: ""
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> {}
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRefreshing = true)
            when (val result = marketRepository.refreshDailyPicks()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isRefreshing = false,
                        picks = result.data?.picks ?: _state.value.picks,
                        timestamp = result.data?.timestamp ?: _state.value.timestamp
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isRefreshing = false)
                }
                else -> {}
            }
        }
    }
}
