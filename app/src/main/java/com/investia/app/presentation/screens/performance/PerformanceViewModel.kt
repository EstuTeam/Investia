package com.investia.app.presentation.screens.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.domain.model.BacktestResult
import com.investia.app.domain.repository.BacktestRepository
import com.investia.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PerformanceState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val backtestResult: BacktestResult? = null,
    val selectedPeriod: String = "30"
)

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val backtestRepository: BacktestRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PerformanceState())
    val state: StateFlow<PerformanceState> = _state.asStateFlow()

    init {
        loadPerformance()
    }

    fun loadPerformance() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val days = _state.value.selectedPeriod.toIntOrNull() ?: 30
            val result = backtestRepository.runBacktest(days)
            _state.value = _state.value.copy(
                isLoading = false,
                backtestResult = (result as? Resource.Success)?.data,
                error = (result as? Resource.Error)?.message
            )
        }
    }

    fun setPeriod(period: String) {
        _state.value = _state.value.copy(selectedPeriod = period)
        loadPerformance()
    }
}
