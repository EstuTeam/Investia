package com.investia.app.presentation.screens.signals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.domain.model.SignalData
import com.investia.app.domain.repository.MarketRepository
import com.investia.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignalCenterState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val signals: Map<String, SignalData> = emptyMap()
)

@HiltViewModel
class SignalCenterViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignalCenterState())
    val state: StateFlow<SignalCenterState> = _state.asStateFlow()

    private val bist30 = listOf(
        "THYAO", "GARAN", "AKBNK", "YKBNK", "EREGL", "BIMAS", "ASELS",
        "KCHOL", "SAHOL", "SISE", "TCELL", "TUPRS", "PGSUS", "FROTO", "TOASO"
    )

    init {
        loadSignals()
    }

    fun loadSignals() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val results = bist30.map { symbol ->
                async {
                    val result = marketRepository.getStockSignals("$symbol.IS")
                    if (result is Resource.Success && result.data != null) {
                        symbol to result.data
                    } else null
                }
            }.awaitAll().filterNotNull().toMap()

            _state.value = _state.value.copy(
                isLoading = false,
                signals = results,
                error = if (results.isEmpty()) "Sinyal verisi alınamadı" else null
            )
        }
    }
}
