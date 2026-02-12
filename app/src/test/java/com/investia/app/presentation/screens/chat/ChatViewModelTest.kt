package com.investia.app.presentation.screens.chat

import app.cash.turbine.test
import com.investia.app.domain.repository.AIChatRepository
import com.investia.app.util.Resource
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
class ChatViewModelTest {

    private lateinit var viewModel: ChatViewModel
    private val aiChatRepository: AIChatRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(aiChatRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendMessage adds user message and AI response`() = runTest {
        coEvery { aiChatRepository.sendChatMessage(any(), any()) } returns Resource.Success("THYAO için analiz pozitif görünüyor")

        viewModel.sendMessage("THYAO hakkında ne düşünüyorsun?")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.messages.size)
            assertEquals("user", state.messages[0].role)
            assertEquals("THYAO hakkında ne düşünüyorsun?", state.messages[0].content)
            assertEquals("assistant", state.messages[1].role)
            assertFalse(state.isTyping)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendMessage shows error message on failure`() = runTest {
        coEvery { aiChatRepository.sendChatMessage(any(), any()) } returns Resource.Error("Sunucu hatası")

        viewModel.sendMessage("Merhaba")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.messages.size)
            assertEquals("assistant", state.messages[1].role)
            assertTrue(state.messages[1].content.contains("hata"))
            assertFalse(state.isTyping)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial state has empty messages`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.messages.isEmpty())
            assertFalse(state.isTyping)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
