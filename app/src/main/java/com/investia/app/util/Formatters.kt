package com.investia.app.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object Formatters {
    private val trLocale = Locale("tr", "TR")

    private val priceFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(trLocale))
    private val percentFormat = DecimalFormat("+0.00;-0.00")
    private val volumeFormat = DecimalFormat("#,###", DecimalFormatSymbols(trLocale))
    private val scoreFormat = DecimalFormat("0")

    fun formatPrice(value: Double): String = "${priceFormat.format(value)} ₺"
    fun formatPriceShort(value: Double): String = priceFormat.format(value)
    fun formatPercent(value: Double): String = "${percentFormat.format(value)}%"
    fun formatVolume(value: Long): String = volumeFormat.format(value)
    fun formatScore(value: Int): String = scoreFormat.format(value)

    fun formatIndexPrice(value: Double, symbol: String): String {
        return when {
            symbol.startsWith("XU") -> DecimalFormat("#,###.00", DecimalFormatSymbols(trLocale)).format(value)
            symbol.contains("TRY") -> "₺${DecimalFormat("#,##0.0000", DecimalFormatSymbols(trLocale)).format(value)}"
            symbol.contains("BTC") -> "\$${DecimalFormat("#,###", DecimalFormatSymbols(trLocale)).format(value)}"
            symbol.contains("GC") || symbol.contains("GOLD") -> "\$${DecimalFormat("#,###.00", DecimalFormatSymbols(trLocale)).format(value)}"
            else -> priceFormat.format(value)
        }
    }

    fun formatLargeNumber(value: Double): String {
        return when {
            value >= 1_000_000_000 -> "${priceFormat.format(value / 1_000_000_000)} Milyar ₺"
            value >= 1_000_000 -> "${priceFormat.format(value / 1_000_000)} Milyon ₺"
            value >= 1_000 -> "${priceFormat.format(value / 1_000)} Bin ₺"
            else -> formatPrice(value)
        }
    }

    fun formatPnL(value: Double): String {
        val prefix = if (value >= 0) "+" else ""
        return "$prefix${priceFormat.format(value)} ₺"
    }
}
