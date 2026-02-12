package com.investia.app.presentation.screens.signals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.domain.model.DailyPicksResponse
import com.investia.app.domain.model.SignalData
import com.investia.app.domain.model.StockPick
import com.investia.app.domain.repository.MarketRepository
import com.investia.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignalCenterState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val signals: Map<String, SignalData> = emptyMap(),
    val picks: List<StockPick> = emptyList(),
    val marketStatus: String = ""
)

@HiltViewModel
class SignalCenterViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignalCenterState())
    val state: StateFlow<SignalCenterState> = _state.asStateFlow()

    init {
        loadSignals()
    }

    fun loadSignals() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            // Use daily-picks endpoint for batch loading (single API call instead of 15)
            val result = marketRepository.getDailyPicks()
            
            when (result) {
                is Resource.Success -> {
                    val picks = result.data?.picks ?: emptyList()
                    // Convert picks to signal map for backward compatibility
                    val signalMap = picks.associate { pick ->
                        pick.symbol to SignalData(
                            symbol = pick.symbol,
                            signal = pick.signal,
                            action = pick.signal.name,
                            price = pick.price,
                            changePercent = pick.changePercent,
                            rsi = pick.rsi,
                            macdSignal = "",
                            score = pick.score
                        )
                    }
                    
                    _state.value = _state.value.copy(
                        isLoading = false,
                        signals = signalMap,
                        picks = picks,
                        marketStatus = result.data?.marketStatus ?: "",
                        error = if (picks.isEmpty()) "Şu an aktif sinyal bulunamadı" else null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Sinyal verisi alınamadı"
                    )
                }
                is Resource.Loading -> {
                    // Already handled above
                }
            }
        }
    }
}
