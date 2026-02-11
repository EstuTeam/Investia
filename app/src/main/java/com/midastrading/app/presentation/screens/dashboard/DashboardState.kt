package com.midastrading.app.presentation.screens.dashboard

import com.midastrading.app.domain.model.*

data class DashboardState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val marketOverview: MarketOverview? = null,
    val marketIndices: List<MarketIndex> = emptyList(),
    val topPicks: List<StockPick> = emptyList(),
    val portfolioSummary: PortfolioSummary? = null,
    val isRefreshing: Boolean = false
)
