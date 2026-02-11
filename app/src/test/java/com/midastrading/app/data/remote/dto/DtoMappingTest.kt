package com.midastrading.app.data.remote.dto

import com.midastrading.app.domain.model.SignalType
import org.junit.Assert.*
import org.junit.Test

class DtoMappingTest {

    @Test
    fun `StockQuoteDto maps snake_case and camelCase fields correctly`() {
        val dto = StockQuoteDto(
            symbol = "THYAO",
            name = "Türk Hava Yolları",
            price = 280.5,
            previousClose = 275.0,
            change = 5.5,
            changePercent = 0.0,
            changePercentSnake = 2.0,
            volume = 15000000,
            marketCap = 0,
            marketCapSnake = 150000000000,
            dayHigh = 282.0,
            dayHighSnake = 0.0,
            dayLow = 0.0,
            dayLowSnake = 274.5
        )

        val domain = dto.toDomain()
        assertEquals("THYAO", domain.symbol)
        assertEquals(280.5, domain.price, 0.01)
        assertEquals(2.0, domain.changePercent, 0.01)  // snake_case fallback
        assertEquals(150000000000, domain.marketCap)    // snake_case fallback
        assertEquals(282.0, domain.dayHigh, 0.01)       // camelCase primary
        assertEquals(274.5, domain.dayLow, 0.01)        // snake_case fallback
    }

    @Test
    fun `StockPickDto removes IS suffix from ticker`() {
        val dto = StockPickDto(
            ticker = "AKBNK.IS",
            symbol = "",
            name = "Akbank",
            strength = 90,
            score = 50,
            entryPrice = 48.5,
            price = 0.0
        )

        val domain = dto.toDomain()
        assertEquals("AKBNK", domain.symbol)
        assertEquals(90, domain.score)      // strength > 0 → use strength
        assertEquals(48.5, domain.price, 0.01) // entryPrice > 0 → use entryPrice
    }

    @Test
    fun `StockPickDto falls back to symbol and score when ticker and strength empty`() {
        val dto = StockPickDto(
            ticker = "",
            symbol = "GARAN",
            name = "Garanti",
            strength = 0,
            score = 75,
            entryPrice = 0.0,
            price = 120.0
        )

        val domain = dto.toDomain()
        assertEquals("GARAN", domain.symbol)
        assertEquals(75, domain.score)
        assertEquals(120.0, domain.price, 0.01)
    }

    @Test
    fun `DailyPicksDto maps market trend and strategy info`() {
        val dto = DailyPicksDto(
            status = "success",
            picks = emptyList(),
            totalScanned = 20,
            found = 0,
            strategyInfo = StrategyInfoDto(
                name = "Hybrid Strategy v4",
                winRate = "57.1%",
                profitFactor = "1.94",
                backtestReturn = "+105.31%"
            ),
            marketTrend = "YATAY",
            timestamp = "2024-01-01"
        )

        val domain = dto.toDomain()
        assertEquals("YATAY", domain.marketStatus)
        assertEquals("Hybrid Strategy v4", domain.strategy)
    }

    @Test
    fun `SignalDataDto maps signal type safely`() {
        val buyDto = SignalDataDto(symbol = "THYAO", signal = "BUY")
        assertEquals(SignalType.BUY, buyDto.toDomain().signal)

        val invalidDto = SignalDataDto(symbol = "XYZ", signal = "INVALID")
        assertEquals(SignalType.HOLD, invalidDto.toDomain().signal)
    }

    @Test
    fun `AuthResponseDto falls back from token to access_token`() {
        val dto = AuthResponseDto(
            success = true,
            token = "",
            accessToken = "jwt-access-token",
            refreshToken = "jwt-refresh-token",
            message = "OK"
        )

        val domain = dto.toDomain()
        assertEquals("jwt-access-token", domain.token)
        assertEquals("jwt-refresh-token", domain.refreshToken)
    }

    @Test
    fun `IPOItemDto maps with fallback fields`() {
        val dto = IPOItemDto(
            symbol = "TEST",
            name = "",
            companyName = "Test Şirketi",
            sector = "Teknoloji",
            status = "upcoming",
            demandStart = "2024-03-01",
            demandEnd = null,
            startDate = "",
            endDate = "2024-03-15",
            finalPrice = 0.0,
            price = 25.0,
            priceRangeMin = 20.0,
            priceRangeMax = 30.0
        )

        val domain = dto.toDomain()
        assertEquals("Test Şirketi", domain.companyName)
        assertEquals("2024-03-01", domain.startDate)
        assertEquals("2024-03-15", domain.endDate)   // demandEnd null → fallback to endDate
        assertEquals(25.0, domain.price, 0.01)        // finalPrice 0 → fallback to price
        assertTrue(domain.priceRange.contains("₺20"))
    }

    @Test
    fun `MarketOverviewDto maps stock list correctly`() {
        val dto = MarketOverviewDto(
            stocks = listOf(
                MarketStockDto(
                    symbol = "THYAO.IS",
                    name = "TURK HAVA YOLLARI",
                    price = 329.0,
                    change = -0.5,
                    changePercent = -0.15,
                    volume = 39050480
                )
            ),
            count = 1,
            summary = MarketSummaryDto(
                advancing = 4,
                declining = 16,
                avgChange = -1.08,
                totalVolume = 835077836,
                marketTrend = "bearish"
            ),
            timestamp = "2024-01-01T12:00:00"
        )

        val domain = dto.toDomain()
        assertEquals(1, domain.stocks.size)
        assertEquals("THYAO", domain.stocks[0].symbol)
        assertEquals(329.0, domain.stocks[0].price, 0.01)
        assertEquals("bearish", domain.summary?.marketTrend)
    }
}
