package com.midastrading.app.presentation.screens.backtest

import app.cash.turbine.test
import com.midastrading.app.domain.model.BacktestResult
import com.midastrading.app.domain.repository.BacktestRepository
import com.midastrading.app.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BacktestViewModelTest {

    private lateinit var viewModel: BacktestViewModel
    private val backtestRepository: BacktestRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private val fakeResult = BacktestResult(
        totalTrades = 25,
        winRate = 68.0,
        profitFactor = 2.1,
        totalReturn = 15.5,
        maxDrawdown = 8.3,
        sharpeRatio = 1.8,
        trades = emptyList(),
        equityCurve = listOf(100.0, 102.0, 105.0, 103.0, 108.0, 115.5)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load runs quick backtest`() = runTest {
        coEvery { backtestRepository.quickBacktest() } returns Resource.Success(fakeResult)

        viewModel = BacktestViewModel(backtestRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isRunning)
            assertNotNull(state.result)
            assertEquals(25, state.result?.totalTrades)
            assertEquals(68.0, state.result?.winRate ?: 0.0, 0.01)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `runBacktest with custom days`() = runTest {
        coEvery { backtestRepository.quickBacktest() } returns Resource.Success(fakeResult)
        coEvery { backtestRepository.runBacktest(60) } returns Resource.Success(
            fakeResult.copy(totalTrades = 50, winRate = 72.0)
        )

        viewModel = BacktestViewModel(backtestRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.runBacktest(60)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(60, state.selectedDays)
            assertEquals(50, state.result?.totalTrades)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error state when backtest fails`() = runTest {
        coEvery { backtestRepository.quickBacktest() } returns Resource.Error("Backtest başarısız")

        viewModel = BacktestViewModel(backtestRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.error)
            assertEquals("Backtest başarısız", state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
