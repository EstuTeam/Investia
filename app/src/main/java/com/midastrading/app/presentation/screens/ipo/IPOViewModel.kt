package com.midastrading.app.presentation.screens.ipo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midastrading.app.domain.model.IPOItem
import com.midastrading.app.domain.model.IPOStats
import com.midastrading.app.domain.repository.IPORepository
import com.midastrading.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IPOState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val allIPOs: List<IPOItem> = emptyList(),
    val stats: IPOStats? = null,
    val selectedTab: Int = 0 // 0=All, 1=Active, 2=Upcoming, 3=Completed
)

@HiltViewModel
class IPOViewModel @Inject constructor(
    private val ipoRepository: IPORepository
) : ViewModel() {

    private val _state = MutableStateFlow(IPOState())
    val state: StateFlow<IPOState> = _state.asStateFlow()

    init {
        loadIPOs()
    }

    fun loadIPOs() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val iposJob = async { ipoRepository.getIPOList() }
            val statsJob = async { ipoRepository.getIPOStats() }

            val iposResult = iposJob.await()
            val statsResult = statsJob.await()

            _state.value = _state.value.copy(
                isLoading = false,
                allIPOs = (iposResult as? Resource.Success)?.data ?: emptyList(),
                stats = (statsResult as? Resource.Success)?.data,
                error = if (iposResult is Resource.Error) iposResult.message else null
            )
        }
    }

    fun selectTab(tab: Int) {
        _state.value = _state.value.copy(selectedTab = tab)
    }

    val filteredIPOs: List<IPOItem>
        get() {
            val ipos = _state.value.allIPOs
            return when (_state.value.selectedTab) {
                1 -> ipos.filter { it.status.name == "ACTIVE" }
                2 -> ipos.filter { it.status.name == "UPCOMING" }
                3 -> ipos.filter { it.status.name == "COMPLETED" || it.status.name == "TRADING" }
                else -> ipos
            }
        }
}
