package com.midastrading.app.presentation.screens.screener

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midastrading.app.domain.model.StockPick
import com.midastrading.app.domain.repository.MarketRepository
import com.midastrading.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScreenerState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val results: List<StockPick> = emptyList(),
    val sortBy: String = "score"
)

@HiltViewModel
class ScreenerViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScreenerState())
    val state: StateFlow<ScreenerState> = _state.asStateFlow()

    init { loadScreener() }

    fun loadScreener() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = marketRepository.getScreener()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        results = result.data?.sortedByDescending { it.score } ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
                else -> {}
            }
        }
    }
}
