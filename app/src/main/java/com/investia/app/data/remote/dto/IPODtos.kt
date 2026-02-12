package com.investia.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.investia.app.domain.model.*

// ===== IPO =====
data class IPOListDto(
    val success: Boolean = false,
    val ipos: List<IPOItemDto> = emptyList(),
    val count: Int = 0
)

data class IPOItemDto(
    val id: String = "",
    val symbol: String = "",
    val name: String = "",
    @SerializedName("company_name") val companyName: String = "",
    val sector: String = "",
    val status: String = "upcoming",
    val description: String = "",
    @SerializedName("demand_start") val demandStart: String? = null,
    @SerializedName("demand_end") val demandEnd: String? = null,
    @SerializedName("trading_start") val tradingStart: String? = null,
    @SerializedName("start_date") val startDate: String = "",
    @SerializedName("end_date") val endDate: String = "",
    @SerializedName("final_price") val finalPrice: Double = 0.0,
    val price: Double = 0.0,
    @SerializedName("price_range_min") val priceRangeMin: Double = 0.0,
    @SerializedName("price_range_max") val priceRangeMax: Double = 0.0,
    @SerializedName("price_range") val priceRange: String = "",
    @SerializedName("lot_size") val lotSize: Long = 0,
    @SerializedName("total_shares") val totalShares: Long = 0,
    @SerializedName("demand_multiple") val demandMultiple: Double? = null,
    @SerializedName("current_price") val currentPrice: Double = 0.0,
    @SerializedName("total_return_percent") val totalReturnPercent: Double = 0.0,
    @SerializedName("return_percent") val returnPercent: Double = 0.0,
    @SerializedName("daily_change_percent") val dailyChangePercent: Double = 0.0,
    @SerializedName("lead_manager") val leadManager: String = "",
    @SerializedName("distribution_method") val distributionMethod: String = ""
) {
    fun toDomain() = IPOItem(
        symbol = symbol,
        companyName = name.ifBlank { companyName },
        sector = sector,
        status = try { IPOStatus.valueOf(status.uppercase()) } catch (e: Exception) { IPOStatus.UPCOMING },
        startDate = demandStart ?: startDate,
        endDate = demandEnd ?: endDate,
        price = if (finalPrice > 0) finalPrice else price,
        priceRange = if (priceRange.isNotBlank()) priceRange
                     else if (priceRangeMin > 0) "₺%.2f - ₺%.2f".format(priceRangeMin, priceRangeMax)
                     else "",
        lotSize = lotSize.toInt(),
        totalShares = totalShares,
        demandMultiple = demandMultiple ?: 0.0,
        description = description,
        currentPrice = currentPrice,
        returnPercent = if (totalReturnPercent != 0.0) totalReturnPercent else returnPercent
    )
}

data class IPOStatsDto(
    val success: Boolean = false,
    val stats: IPOStatsInnerDto? = null
) {
    fun toDomain() = stats?.toDomain() ?: IPOStats()
}

data class IPOStatsInnerDto(
    @SerializedName("total_ipos") val totalIPOs: Int = 0,
    @SerializedName("active_count") val activeCount: Int = 0,
    @SerializedName("upcoming_count") val upcomingCount: Int = 0,
    @SerializedName("trading_count") val tradingCount: Int = 0,
    @SerializedName("completed_count") val completedCount: Int = 0,
    @SerializedName("avg_performance_percent") val avgPerformancePercent: Double = 0.0,
    @SerializedName("active_ipos") val activeIPOs: Int = 0,
    @SerializedName("upcoming_ipos") val upcomingIPOs: Int = 0,
    @SerializedName("avg_return") val avgReturn: Double = 0.0
) {
    fun toDomain() = IPOStats(
        totalIPOs = totalIPOs,
        activeIPOs = if (activeCount > 0) activeCount else activeIPOs,
        upcomingIPOs = if (upcomingCount > 0) upcomingCount else upcomingIPOs,
        avgReturn = if (avgPerformancePercent != 0.0) avgPerformancePercent else avgReturn
    )
}
