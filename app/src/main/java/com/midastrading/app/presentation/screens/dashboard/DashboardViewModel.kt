package com.midastrading.app.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midastrading.app.domain.model.MarketIndex
import com.midastrading.app.domain.repository.MarketRepository
import com.midastrading.app.util.ConnectivityMonitor
import com.midastrading.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val connectivity: ConnectivityMonitor
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    val isConnected: StateFlow<Boolean> = connectivity.isConnected

    private val indexSymbols = listOf(
        "XU100.IS" to "BIST 100",
        "XU030.IS" to "BIST 30",
        "USDTRY=X" to "USD/TRY",
        "EURTRY=X" to "EUR/TRY",
        "BTC-USD" to "Bitcoin",
        "GC=F" to "Altın"
    )

    init {
        loadDashboard()
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivity.connectionState.collect { connected ->
                if (connected && _state.value.error != null) {
                    delay(1000)
                    loadDashboard()
                }
            }
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            // Load market overview, daily picks, and indices in parallel
            val marketJob = async { marketRepository.getMarketOverview() }
            val picksJob = async { marketRepository.getDailyPicks() }
            val indicesJob = async { loadMarketIndices() }

            val marketResult = marketJob.await()
            val picksResult = picksJob.await()
            val indices = indicesJob.await()

            _state.value = _state.value.copy(
                isLoading = false,
                isRefreshing = false,
                marketOverview = when (marketResult) {
                    is Resource.Success -> marketResult.data
                    else -> _state.value.marketOverview
                },
                marketIndices = indices.ifEmpty { _state.value.marketIndices },
                topPicks = when (picksResult) {
                    is Resource.Success -> picksResult.data?.picks ?: emptyList()
                    else -> _state.value.topPicks
                },
                error = when {
                    marketResult is Resource.Error && picksResult is Resource.Error ->
                        marketResult.message
                    marketResult is Resource.Error -> marketResult.message
                    picksResult is Resource.Error -> picksResult.message
                    else -> null
                }
            )
        }
    }

    private suspend fun loadMarketIndices(): List<MarketIndex> {
        return indexSymbols.map { (symbol, name) ->
            viewModelScope.async {
                when (val result = marketRepository.getMarketIndex(symbol, name)) {
                    is Resource.Success -> result.data
                    else -> null
                }
            }
        }.awaitAll().filterNotNull()
    }

    fun refresh() {
        _state.value = _state.value.copy(isRefreshing = true)
        loadDashboard()
    }
}
