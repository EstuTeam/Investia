package com.investia.app.presentation.screens.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.domain.model.Portfolio
import com.investia.app.domain.model.WatchlistItem
import com.investia.app.domain.repository.PortfolioRepository
import com.investia.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PortfolioState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val portfolios: List<Portfolio> = emptyList(),
    val watchlists: List<WatchlistItem> = emptyList(),
    val selectedPortfolio: Portfolio? = null,
    val showCreateDialog: Boolean = false,
    val showTransactionDialog: Boolean = false
)

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState())
    val state: StateFlow<PortfolioState> = _state.asStateFlow()

    init {
        loadPortfolios()
    }

    fun loadPortfolios() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = portfolioRepository.getPortfolios()
            _state.value = _state.value.copy(
                isLoading = false,
                portfolios = (result as? Resource.Success)?.data ?: emptyList(),
                selectedPortfolio = (result as? Resource.Success)?.data?.firstOrNull(),
                error = (result as? Resource.Error)?.message
            )
        }
    }

    fun loadWatchlists() {
        viewModelScope.launch {
            val result = portfolioRepository.getWatchlists()
            _state.value = _state.value.copy(
                watchlists = (result as? Resource.Success)?.data ?: emptyList()
            )
        }
    }

    fun createPortfolio(name: String, description: String?) {
        viewModelScope.launch {
            portfolioRepository.createPortfolio(name, description)
            _state.value = _state.value.copy(showCreateDialog = false)
            loadPortfolios()
        }
    }

    fun deletePortfolio(id: Int) {
        viewModelScope.launch {
            portfolioRepository.deletePortfolio(id)
            loadPortfolios()
        }
    }

    fun addTransaction(portfolioId: Int, symbol: String, type: String, quantity: Double, price: Double) {
        viewModelScope.launch {
            portfolioRepository.addTransaction(portfolioId, symbol, type, quantity, price)
            _state.value = _state.value.copy(showTransactionDialog = false)
            loadPortfolios()
        }
    }

    fun selectPortfolio(portfolio: Portfolio) {
        _state.value = _state.value.copy(selectedPortfolio = portfolio)
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = false)
    }

    fun showTransactionDialog() {
        _state.value = _state.value.copy(showTransactionDialog = true)
    }

    fun hideTransactionDialog() {
        _state.value = _state.value.copy(showTransactionDialog = false)
    }
}
