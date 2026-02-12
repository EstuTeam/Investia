package com.investia.app.presentation.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.domain.model.NewsItem
import com.investia.app.domain.repository.NewsRepository
import com.investia.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val news: List<NewsItem> = emptyList(),
    val selectedCategory: String = "economy"
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewsState())
    val state: StateFlow<NewsState> = _state.asStateFlow()

    init {
        loadNews("economy")
    }

    fun loadNews(category: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, selectedCategory = category)
            when (val result = newsRepository.getNews(category)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        news = result.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> {}
            }
        }
    }

    fun selectCategory(index: Int) {
        val categories = listOf("economy", "general", "finance")
        val category = categories.getOrElse(index) { "economy" }
        loadNews(category)
    }
}
