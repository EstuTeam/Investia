package com.investia.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.investia.app.domain.model.*

// ===== Stocks =====
data class StockQuoteDto(
    val symbol: String = "",
    val name: String = "",
    val price: Double = 0.0,
    @SerializedName("previousClose") val previousClose: Double = 0.0,
    val change: Double = 0.0,
    @SerializedName("changePercent") val changePercent: Double = 0.0,
    @SerializedName("change_percent") val changePercentSnake: Double = 0.0,
    val volume: Long = 0,
    @SerializedName("marketCap") val marketCap: Long = 0,
    @SerializedName("market_cap") val marketCapSnake: Long = 0,
    @SerializedName("dayHigh") val dayHigh: Double = 0.0,
    @SerializedName("day_high") val dayHighSnake: Double = 0.0,
    @SerializedName("dayLow") val dayLow: Double = 0.0,
    @SerializedName("day_low") val dayLowSnake: Double = 0.0
) {
    fun toDomain() = StockQuote(
        symbol = symbol,
        name = name,
        price = price,
        previousClose = previousClose,
        change = change,
        changePercent = if (changePercent != 0.0) changePercent else changePercentSnake,
        volume = volume,
        marketCap = if (marketCap != 0L) marketCap else marketCapSnake,
        dayHigh = if (dayHigh != 0.0) dayHigh else dayHighSnake,
        dayLow = if (dayLow != 0.0) dayLow else dayLowSnake
    )
}

// Backend /api/stocks/{symbol}/data returns:
// { symbol, interval, period, data: [{timestamp, open, high, low, close, volume}], isMockData }
data class StockHistoryDto(
    val symbol: String = "",
    val interval: String = "",
    val period: String = "",
    val data: List<PricePointDto> = emptyList(),
    val prices: List<PricePointDto> = emptyList(),
    val isMockData: Boolean = false
) {
    fun getAllPrices(): List<PricePointDto> = data.ifEmpty { prices }
}

data class PricePointDto(
    val date: String = "",
    val timestamp: Long = 0,
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val close: Double = 0.0,
    val volume: Long = 0
)

// ===== Daily Picks =====
// Backend /api/signals/daily-picks returns:
// { status, picks: [...], total_scanned, found, strategy_info: {name, win_rate, ...},
//   market_trend, warnings: [...], timestamp, cache_source }
data class DailyPicksDto(
    val status: String = "",
    val picks: List<StockPickDto> = emptyList(),
    @SerializedName("total_scanned") val totalScanned: Int = 0,
    val found: Int = 0,
    @SerializedName("strategy_info") val strategyInfo: StrategyInfoDto? = null,
    @SerializedName("market_trend") val marketTrend: String = "",
    val warnings: List<String> = emptyList(),
    val timestamp: String = "",
    @SerializedName("cache_source") val cacheSource: String = ""
) {
    fun toDomain() = DailyPicksResponse(
        picks = picks.map { it.toDomain() },
        strategy = strategyInfo?.name ?: "",
        timestamp = timestamp,
        marketStatus = marketTrend.ifBlank { "unknown" }
    )
}

data class StrategyInfoDto(
    val name: String = "",
    @SerializedName("win_rate") val winRate: String = "",
    @SerializedName("profit_factor") val profitFactor: String = "",
    @SerializedName("backtest_return") val backtestReturn: String = ""
)

data class StockPickDto(
    val ticker: String = "",
    val symbol: String = "",
    val name: String = "",
    val strength: Int = 0,
    val confidence: Int = 0,
    val score: Int = 0,
    @SerializedName("entry_price") val entryPrice: Double = 0.0,
    val price: Double = 0.0,
    @SerializedName("change_percent") val changePercent: Double = 0.0,
    @SerializedName("stop_loss") val stopLoss: Double = 0.0,
    @SerializedName("take_profit") val takeProfit: Double = 0.0,
    @SerializedName("take_profit_1") val takeProfit1: Double = 0.0,
    @SerializedName("take_profit_2") val takeProfit2: Double = 0.0,
    @SerializedName("risk_pct") val riskPct: Double = 0.0,
    @SerializedName("risk_reward_ratio") val riskRewardRatio: Double = 0.0,
    val rsi: Double = 0.0,
    val signal: String = "HOLD",
    val reasons: List<String> = emptyList(),
    val sector: String = "",
    @SerializedName("reward_pct") val rewardPct: Double = 0.0
) {
    fun toDomain() = StockPick(
        symbol = (ticker.ifBlank { symbol }).removeSuffix(".IS"),
        name = name,
        score = if (strength > 0) strength else score,
        price = if (entryPrice > 0) entryPrice else price,
        changePercent = changePercent,
        stopLoss = stopLoss,
        takeProfit1 = if (takeProfit1 > 0) takeProfit1 else takeProfit,
        takeProfit2 = takeProfit2,
        riskPercent = riskPct,
        rsi = rsi,
        signal = when (signal.uppercase()) {
            "STRONG_BUY" -> SignalType.STRONG_BUY
            "BUY" -> SignalType.BUY
            "SELL" -> SignalType.SELL
            "STRONG_SELL" -> SignalType.STRONG_SELL
            else -> SignalType.HOLD
        },
        reasons = reasons,
        sector = sector
    )
}

// ===== Signals =====
// Backend /api/signals/{symbol} returns:
// { symbol, signal, action, price, entry_price, change, changePercent, 
//   confidence, strength, score, strategy, rsi, macd_signal, timestamp }
data class SignalDataDto(
    val symbol: String = "",
    val ticker: String = "",
    val signal: String = "HOLD",
    val action: String = "",
    val price: Double = 0.0,
    @SerializedName("entry_price") val entryPrice: Double = 0.0,
    val change: Double = 0.0,
    val changePercent: Double = 0.0,
    @SerializedName("change_percent") val changePercentSnake: Double = 0.0,
    val confidence: Double = 0.0,
    val strength: Int = 0,
    val strategy: String = "",
    val rsi: Double = 0.0,
    @SerializedName("macd_signal") val macdSignal: String = "",
    val score: Int = 0,
    @SerializedName("stop_loss") val stopLoss: Double = 0.0,
    @SerializedName("take_profit") val takeProfit: Double = 0.0,
    val sector: String = "",
    val reasons: List<String> = emptyList(),
    val timestamp: String = ""
) {
    fun toDomain() = SignalData(
        symbol = symbol.ifBlank { ticker.removeSuffix(".IS") },
        signal = try { SignalType.valueOf((signal.ifBlank { action }).uppercase()) } catch (e: Exception) { SignalType.HOLD },
        action = action.ifBlank { signal },
        price = if (price > 0) price else entryPrice,
        changePercent = if (changePercent != 0.0) changePercent else changePercentSnake,
        rsi = rsi,
        macdSignal = macdSignal,
        score = when {
            score > 0 -> score
            strength > 0 -> strength
            else -> confidence.toInt()
        }
    )
}

// ===== Screener =====
data class ScreenerResponseDto(
    val success: Boolean = false,
    val opportunities: List<StockPickDto> = emptyList(),
    val picks: List<StockPickDto> = emptyList(),
    val stocks: List<ScreenerStockDto> = emptyList(),
    val gainers: List<MoverStockDto> = emptyList(),
    val losers: List<MoverStockDto> = emptyList(),
    @SerializedName("scan_time") val scanTime: String = "",
    @SerializedName("total_picks") val totalPicks: Int = 0,
    @SerializedName("total_stocks") val totalStocks: Int = 0,
    val timestamp: String = "",
    val source: String = ""
)

data class MoverStockDto(
    val symbol: String = "",
    val name: String = "",
    val sector: String = "",
    val price: Double = 0.0,
    val change: Double = 0.0,
    @SerializedName("change_percent") val changePercent: Double = 0.0,
    val volume: Long = 0,
    @SerializedName("volume_ratio") val volumeRatio: Double = 0.0
) {
    fun toDomain() = StockPick(
        symbol = symbol.removeSuffix(".IS"),
        name = name,
        score = (kotlin.math.abs(changePercent) * 10).toInt().coerceIn(0, 100),
        price = price,
        changePercent = changePercent,
        stopLoss = 0.0,
        takeProfit1 = 0.0,
        riskPercent = 0.0,
        signal = if (changePercent > 0) SignalType.BUY else SignalType.SELL,
        sector = sector
    )
}

data class ScreenerStockDto(
    val ticker: String = "",
    val score: Int = 0,
    @SerializedName("setup_quality") val setupQuality: String = "",
    val recommendation: String = "",
    val momentum: String = "",
    val price: Double = 0.0,
    @SerializedName("current_price") val currentPrice: Double = 0.0,
    @SerializedName("change_percent") val changePercent: Double = 0.0,
    val sector: String = "",
    val levels: ScreenerLevelsDto? = null
) {
    fun toDomain() = StockPick(
        symbol = ticker.removeSuffix(".IS"),
        name = "",
        score = score,
        price = if (currentPrice > 0) currentPrice else price,
        changePercent = changePercent,
        stopLoss = levels?.stopLoss ?: 0.0,
        takeProfit1 = levels?.takeProfit1 ?: 0.0,
        takeProfit2 = levels?.takeProfit2 ?: 0.0,
        riskPercent = levels?.riskPct ?: 0.0,
        rsi = 0.0,
        signal = when (recommendation.uppercase()) {
            "BUY" -> SignalType.BUY
            "STRONG_BUY" -> SignalType.STRONG_BUY
            "SELL" -> SignalType.SELL
            "STRONG_SELL" -> SignalType.STRONG_SELL
            else -> SignalType.HOLD
        },
        reasons = emptyList(),
        sector = sector
    )
}

data class ScreenerLevelsDto(
    @SerializedName("entry_price") val entryPrice: Double = 0.0,
    @SerializedName("take_profit") val takeProfit: Double = 0.0,
    @SerializedName("take_profit_1") val takeProfit1: Double = 0.0,
    @SerializedName("take_profit_2") val takeProfit2: Double = 0.0,
    @SerializedName("stop_loss") val stopLoss: Double = 0.0,
    @SerializedName("risk_pct") val riskPct: Double = 0.0,
    @SerializedName("reward_pct") val rewardPct: Double = 0.0,
    @SerializedName("risk_reward_ratio") val riskRewardRatio: Double = 0.0
)

data class TopPicksDto(
    val picks: List<StockPickDto> = emptyList(),
    val stocks: List<ScreenerStockDto> = emptyList(),
    val timestamp: String = ""
)
