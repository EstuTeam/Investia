package com.midastrading.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.midastrading.app.domain.model.*

// ===== Portfolio =====
data class PortfolioListDto(
    val success: Boolean = true,
    val data: List<PortfolioDto> = emptyList()
)

data class PortfolioDto(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    @SerializedName("is_default") val isDefault: Boolean = false,
    val holdings: List<PortfolioItemDto> = emptyList(),
    @SerializedName("total_value") val totalValue: Double = 0.0,
    @SerializedName("total_cost") val totalCost: Double = 0.0
) {
    fun toDomain() = Portfolio(
        id = id, name = name, description = description,
        isDefault = isDefault,
        holdings = holdings.map { it.toDomain() },
        totalValue = totalValue, totalCost = totalCost
    )
}

data class CreatePortfolioDto(
    val name: String,
    val description: String? = null,
    @SerializedName("is_default") val isDefault: Boolean = false
)

data class PortfolioItemDto(
    val symbol: String = "",
    val name: String = "",
    val quantity: Double = 0.0,
    @SerializedName("avg_cost") val avgCost: Double = 0.0,
    @SerializedName("current_price") val currentPrice: Double = 0.0,
    @SerializedName("total_cost") val totalCost: Double = 0.0,
    @SerializedName("total_value") val totalValue: Double = 0.0,
    @SerializedName("profit_loss") val profitLoss: Double = 0.0,
    @SerializedName("profit_loss_percent") val profitLossPercent: Double = 0.0
) {
    fun toDomain() = PortfolioItem(
        symbol = symbol, name = name, quantity = quantity,
        avgCost = avgCost, currentPrice = currentPrice,
        totalCost = totalCost, totalValue = totalValue,
        profitLoss = profitLoss, profitLossPercent = profitLossPercent
    )
}

data class TransactionDto(
    val id: Int = 0,
    @SerializedName("portfolio_id") val portfolioId: Int = 0,
    val symbol: String = "",
    val type: String = "BUY",
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val date: String = "",
    val notes: String = ""
) {
    fun toDomain() = Transaction(
        id = id, portfolioId = portfolioId, symbol = symbol,
        type = try { TransactionType.valueOf(type.uppercase()) } catch (e: Exception) { TransactionType.BUY },
        quantity = quantity, price = price, date = date, notes = notes
    )
}

data class AddTransactionDto(
    val symbol: String,
    val type: String,
    val quantity: Double,
    val price: Double,
    val date: String? = null,
    val notes: String? = null
)

data class WatchlistDto(
    val id: Int = 0,
    val name: String = "",
    val tickers: List<String> = emptyList()
) {
    fun toDomain() = WatchlistItem(id, name, tickers)
}

data class WatchlistsResponseDto(
    val success: Boolean = true,
    val data: List<WatchlistDto> = emptyList()
)

data class CreateWatchlistDto(
    val name: String,
    val tickers: List<String> = emptyList()
)
