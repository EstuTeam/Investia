package com.midastrading.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.midastrading.app.domain.model.*

// ===== Backtest =====
data class BacktestResultDto(
    @SerializedName("total_trades") val totalTrades: Int = 0,
    @SerializedName("win_rate") val winRate: Double = 0.0,
    @SerializedName("profit_factor") val profitFactor: Double = 0.0,
    @SerializedName("total_return") val totalReturn: Double = 0.0,
    @SerializedName("max_drawdown") val maxDrawdown: Double = 0.0,
    @SerializedName("sharpe_ratio") val sharpeRatio: Double = 0.0,
    val trades: List<BacktestTradeDto> = emptyList(),
    @SerializedName("equity_curve") val equityCurve: List<Double> = emptyList()
) {
    fun toDomain() = BacktestResult(
        totalTrades = totalTrades, winRate = winRate,
        profitFactor = profitFactor, totalReturn = totalReturn,
        maxDrawdown = maxDrawdown, sharpeRatio = sharpeRatio,
        trades = trades.map { it.toDomain() }, equityCurve = equityCurve
    )
}

data class BacktestTradeDto(
    val symbol: String = "",
    @SerializedName("entry_date") val entryDate: String = "",
    @SerializedName("exit_date") val exitDate: String = "",
    @SerializedName("entry_price") val entryPrice: Double = 0.0,
    @SerializedName("exit_price") val exitPrice: Double = 0.0,
    val quantity: Double = 0.0,
    @SerializedName("profit_loss") val profitLoss: Double = 0.0,
    @SerializedName("profit_loss_percent") val profitLossPercent: Double = 0.0,
    val signal: String = ""
) {
    fun toDomain() = BacktestTrade(
        symbol = symbol, entryDate = entryDate, exitDate = exitDate,
        entryPrice = entryPrice, exitPrice = exitPrice, quantity = quantity,
        profitLoss = profitLoss, profitLossPercent = profitLossPercent, signal = signal
    )
}

// ===== Advanced Indicators =====
data class IchimokuDto(
    val tenkan: List<Double> = emptyList(),
    val kijun: List<Double> = emptyList(),
    @SerializedName("senkou_a") val senkouA: List<Double> = emptyList(),
    @SerializedName("senkou_b") val senkouB: List<Double> = emptyList(),
    val chikou: List<Double> = emptyList(),
    val signal: String = ""
) {
    fun toDomain() = IchimokuData(tenkan, kijun, senkouA, senkouB, chikou, signal)
}

data class FibonacciDto(
    val levels: Map<String, Double> = emptyMap(),
    val trend: String = "",
    @SerializedName("current_price") val currentPrice: Double = 0.0
) {
    fun toDomain() = FibonacciData(levels, trend, currentPrice)
}

data class BollingerDto(
    val upper: List<Double> = emptyList(),
    val middle: List<Double> = emptyList(),
    val lower: List<Double> = emptyList(),
    val signal: String = ""
) {
    fun toDomain() = BollingerData(upper, middle, lower, signal)
}

// ===== Generic =====
data class HealthDto(
    val status: String = "",
    val timestamp: String = ""
)

data class GenericResponseDto(
    val success: Boolean = false,
    val message: String = ""
)
