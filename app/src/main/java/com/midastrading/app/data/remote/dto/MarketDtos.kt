package com.midastrading.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.midastrading.app.domain.model.*

// ===== Market =====
// Backend /api/market/all returns:
// { stocks: [{symbol, name, price, change, changePercent, volume, high, low, open}],
//   count: 20, summary: {advancing, declining, avgChange, totalVolume, marketTrend}, timestamp }
data class MarketOverviewDto(
    val stocks: List<MarketStockDto> = emptyList(),
    val count: Int = 0,
    val summary: MarketSummaryDto? = null,
    val timestamp: String = ""
) {
    fun toDomain() = MarketOverview(
        stocks = stocks.map { it.toDomain() },
        summary = summary?.toDomain(),
        timestamp = timestamp
    )
}

data class MarketStockDto(
    val symbol: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val change: Double = 0.0,
    val changePercent: Double = 0.0,
    val volume: Long = 0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val open: Double = 0.0
) {
    fun toDomain() = MarketStock(
        symbol = symbol.removeSuffix(".IS"),
        name = name,
        price = price,
        change = change,
        changePercent = changePercent,
        volume = volume,
        high = high,
        low = low,
        open = open
    )
}

data class MarketSummaryDto(
    val advancing: Int = 0,
    val declining: Int = 0,
    val unchanged: Int = 0,
    val avgChange: Double = 0.0,
    val totalVolume: Long = 0,
    val marketTrend: String = ""
) {
    fun toDomain() = MarketSummary(
        advancing = advancing,
        declining = declining,
        avgChange = avgChange,
        totalVolume = totalVolume,
        marketTrend = marketTrend
    )
}
