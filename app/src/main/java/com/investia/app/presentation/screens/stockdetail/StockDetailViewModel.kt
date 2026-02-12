package com.investia.app.presentation.screens.stockdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.domain.model.SignalData
import com.investia.app.domain.model.StockQuote
import com.investia.app.domain.repository.MarketRepository
import com.investia.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StockDetailState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val quote: StockQuote? = null,
    val signal: SignalData? = null
)

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StockDetailState())
    val state: StateFlow<StockDetailState> = _state.asStateFlow()

    fun loadStock(symbol: String) {
        viewModelScope.launch {
            _state.value = StockDetailState(isLoading = true)

            val quoteJob = async { marketRepository.getStockQuote(symbol) }
            val signalJob = async { marketRepository.getStockSignals(symbol) }

            val quoteResult = quoteJob.await()
            val signalResult = signalJob.await()

            _state.value = StockDetailState(
                isLoading = false,
                quote = (quoteResult as? Resource.Success)?.data,
                signal = (signalResult as? Resource.Success)?.data,
                error = if (quoteResult is Resource.Error) quoteResult.message else null
            )
        }
    }
}
