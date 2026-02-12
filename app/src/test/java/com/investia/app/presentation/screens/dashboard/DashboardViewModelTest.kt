package com.investia.app.presentation.screens.dashboard

import app.cash.turbine.test
import com.investia.app.domain.model.*
import com.investia.app.domain.repository.MarketRepository
import com.investia.app.util.ConnectivityMonitor
import com.investia.app.util.Resource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var viewModel: DashboardViewModel
    private val marketRepository: MarketRepository = mockk()
    private val connectivity: ConnectivityMonitor = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val fakeOverview = MarketOverview(
        stocks = listOf(
            MarketStock("THYAO", "TURK HAVA YOLLARI", 329.0, -0.5, -0.15, 39050480),
            MarketStock("GARAN", "GARANTI BANKASI", 155.2, -2.0, -1.27, 26501144)
        ),
        summary = MarketSummary(
            advancing = 4,
            declining = 16,
            avgChange = -1.08,
            totalVolume = 835077836,
            marketTrend = "bearish"
        ),
        timestamp = "2024-01-01T12:00:00"
    )

    private val fakePicks = DailyPicksResponse(
        picks = listOf(
            StockPick(
                symbol = "THYAO",
                name = "Türk Hava Yolları",
                score = 85,
                price = 280.0,
                changePercent = 2.5,
                stopLoss = 270.0,
                takeProfit1 = 295.0,
                takeProfit2 = 310.0,
                riskPercent = 3.5,
                rsi = 62.0,
                signal = SignalType.BUY,
                reasons = listOf("RSI güçlü", "MACD kesişimi"),
                sector = "Havacılık"
            )
        ),
        strategy = "momentum",
        timestamp = "2024-01-01",
        marketStatus = "open"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { connectivity.isConnected } returns MutableStateFlow(true)
        every { connectivity.connectionState } returns flowOf(true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load fetches market overview and daily picks`() = runTest {
        coEvery { marketRepository.getMarketOverview() } returns Resource.Success(fakeOverview)
        coEvery { marketRepository.getDailyPicks() } returns Resource.Success(fakePicks)

        viewModel = DashboardViewModel(marketRepository, connectivity)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.marketOverview)
            assertEquals(2, state.marketOverview?.stocks?.size ?: 0)
            assertEquals("THYAO", state.marketOverview?.stocks?.get(0)?.symbol)
            assertEquals(1, state.topPicks.size)
            assertEquals("THYAO", state.topPicks[0].symbol)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error state when market overview fails`() = runTest {
        coEvery { marketRepository.getMarketOverview() } returns Resource.Error("Bağlantı hatası")
        coEvery { marketRepository.getDailyPicks() } returns Resource.Success(fakePicks)

        viewModel = DashboardViewModel(marketRepository, connectivity)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.error)
            assertEquals("Bağlantı hatası", state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refresh reloads data`() = runTest {
        coEvery { marketRepository.getMarketOverview() } returns Resource.Success(fakeOverview)
        coEvery { marketRepository.getDailyPicks() } returns Resource.Success(fakePicks)

        viewModel = DashboardViewModel(marketRepository, connectivity)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isRefreshing)
            assertNotNull(state.marketOverview)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
