package com.investia.app.presentation.screens.backtest

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

data class BacktestState(
    val isLoading: Boolean = false,
    val isRunning: Boolean = false,
    val error: String? = null,
    val result: BacktestResult? = null,
    val selectedDays: Int = 30
)

@HiltViewModel
class BacktestViewModel @Inject constructor(
    private val backtestRepository: BacktestRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BacktestState())
    val state: StateFlow<BacktestState> = _state.asStateFlow()

    init {
        runQuickBacktest()
    }

    fun runQuickBacktest() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isRunning = true, error = null)
            val result = backtestRepository.quickBacktest()
            _state.value = _state.value.copy(
                isLoading = false,
                isRunning = false,
                result = (result as? Resource.Success)?.data,
                error = (result as? Resource.Error)?.message
            )
        }
    }

    fun runBacktest(days: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isRunning = true, error = null, selectedDays = days)
            val result = backtestRepository.runBacktest(days)
            _state.value = _state.value.copy(
                isLoading = false,
                isRunning = false,
                result = (result as? Resource.Success)?.data,
                error = (result as? Resource.Error)?.message
            )
        }
    }

    fun setDays(days: Int) {
        _state.value = _state.value.copy(selectedDays = days)
    }
}
