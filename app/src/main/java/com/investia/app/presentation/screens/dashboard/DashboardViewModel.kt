package com.investia.app.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.domain.model.MarketIndex
import com.investia.app.domain.repository.AuthRepository
import com.investia.app.domain.repository.MarketRepository
import com.investia.app.domain.repository.PortfolioRepository
import com.investia.app.util.ConnectivityMonitor
import com.investia.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val portfolioRepository: PortfolioRepository,
    private val authRepository: AuthRepository,
    private val connectivity: ConnectivityMonitor
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    val isConnected: StateFlow<Boolean> = connectivity.isConnected

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

            // Load market overview, daily picks, and portfolio in parallel
            val marketJob = async { marketRepository.getMarketOverview() }
            val picksJob = async { marketRepository.getDailyPicks() }
            val portfolioJob = async { loadPortfolioSummary() }

            val marketResult = marketJob.await()
            val picksResult = picksJob.await()
            val portfolioSummary = portfolioJob.await()

            // Extract market indices from market overview (avoids extra API calls)
            val indices = if (marketResult is Resource.Success) {
                marketResult.data?.stocks?.map { stock ->
                    MarketIndex(
                        name = stock.name.ifBlank { stock.symbol },
                        symbol = stock.symbol,
                        price = stock.price,
                        change = stock.change,
                        changePercent = stock.changePercent
                    )
                } ?: emptyList()
            } else {
                _state.value.marketIndices
            }

            _state.value = _state.value.copy(
                isLoading = false,
                isRefreshing = false,
                marketOverview = when (marketResult) {
                    is Resource.Success -> marketResult.data
                    else -> _state.value.marketOverview
                },
                marketIndices = indices,
                topPicks = when (picksResult) {
                    is Resource.Success -> picksResult.data?.picks ?: emptyList()
                    else -> _state.value.topPicks
                },
                portfolioSummary = portfolioSummary ?: _state.value.portfolioSummary,
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

    private suspend fun loadPortfolioSummary(): com.investia.app.domain.model.PortfolioSummary? {
        return try {
            val result = portfolioRepository.getPortfolios()
            if (result is Resource.Success) {
                val portfolios = result.data ?: emptyList()
                val allHoldings = portfolios.flatMap { it.holdings }
                val totalValue = portfolios.sumOf { it.totalValue }
                val totalCost = portfolios.sumOf { it.totalCost }
                com.investia.app.domain.model.PortfolioSummary(
                    totalValue = totalValue,
                    totalCost = totalCost,
                    totalPnL = totalValue - totalCost,
                    totalPnLPercent = if (totalCost > 0) ((totalValue - totalCost) / totalCost) * 100 else 0.0,
                    dailyPnL = 0.0,
                    dailyPnLPercent = 0.0,
                    items = allHoldings
                )
            } else null
        } catch (_: Exception) { null }
    }

    fun refresh() {
        _state.value = _state.value.copy(isRefreshing = true)
        loadDashboard()
    }
}
