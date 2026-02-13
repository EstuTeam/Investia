package com.investia.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.investia.app.domain.model.*

// ===== Market =====
// Backend /api/market/all returns:
// { success: true, data: { bist100: {ticker, price, change, change_percent, is_up, timestamp}, ... }, last_update }

data class MarketIndexItemDto(
    val ticker: String = "",
    val price: Double = 0.0,
    val change: Double = 0.0,
    @SerializedName("change_percent") val changePercent: Double = 0.0,
    @SerializedName("is_up") val isUp: Boolean = true,
    val timestamp: String = "",
    val error: Boolean = false
)

data class MarketDataDto(
    val bist100: MarketIndexItemDto? = null,
    val bist30: MarketIndexItemDto? = null,
    @SerializedName("usd_try") val usdTry: MarketIndexItemDto? = null,
    @SerializedName("eur_try") val eurTry: MarketIndexItemDto? = null,
    val gold: MarketIndexItemDto? = null,
    val btc: MarketIndexItemDto? = null,
    val sp500: MarketIndexItemDto? = null,
    val nasdaq: MarketIndexItemDto? = null
)

data class MarketOverviewDto(
    val success: Boolean = false,
    val data: MarketDataDto? = null,
    @SerializedName("last_update") val lastUpdate: String = "",
    // Legacy fields for backward compatibility
    val stocks: List<MarketStockDto> = emptyList(),
    val count: Int = 0,
    val summary: MarketSummaryDto? = null,
    val timestamp: String = ""
) {
    fun toDomain(): MarketOverview {
        // New format: convert data map to stock list
        if (data != null) {
            val indices = listOfNotNull(
                data.bist100?.toMarketStock("BIST 100"),
                data.bist30?.toMarketStock("BIST 30"),
                data.usdTry?.toMarketStock("USD/TRY"),
                data.eurTry?.toMarketStock("EUR/TRY"),
                data.gold?.toMarketStock("Altın"),
                data.btc?.toMarketStock("Bitcoin"),
                data.sp500?.toMarketStock("S&P 500"),
                data.nasdaq?.toMarketStock("NASDAQ")
            )
            val advancing = indices.count { it.changePercent > 0 }
            val declining = indices.count { it.changePercent < 0 }
            return MarketOverview(
                stocks = indices,
                summary = MarketSummary(
                    advancing = advancing,
                    declining = declining,
                    avgChange = if (indices.isNotEmpty()) indices.map { it.changePercent }.average() else 0.0,
                    totalVolume = 0L,
                    marketTrend = if (advancing > declining) "YUKSELIS" else "DUSUS"
                ),
                timestamp = lastUpdate
            )
        }
        // Legacy format
        return MarketOverview(
            stocks = stocks.map { it.toDomain() },
            summary = summary?.toDomain(),
            timestamp = timestamp
        )
    }
}

private fun MarketIndexItemDto.toMarketStock(displayName: String) = MarketStock(
    symbol = ticker.removeSuffix(".IS"),
    name = displayName,
    price = price,
    change = change,
    changePercent = changePercent,
    volume = 0L,
    high = 0.0,
    low = 0.0,
    open = 0.0
)

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
