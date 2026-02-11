package com.midastrading.app.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midastrading.app.domain.model.ChatMessage
import com.midastrading.app.domain.repository.AIChatRepository
import com.midastrading.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiChatRepository: AIChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun sendMessage(text: String) {
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = "user",
            content = text
        )

        _state.value = _state.value.copy(
            messages = _state.value.messages + userMessage,
            isTyping = true
        )

        viewModelScope.launch {
            when (val result = aiChatRepository.sendChatMessage(text, _state.value.messages)) {
                is Resource.Success -> {
                    val aiMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = "assistant",
                        content = result.data ?: "Yanıt alınamadı"
                    )
                    _state.value = _state.value.copy(
                        messages = _state.value.messages + aiMessage,
                        isTyping = false
                    )
                }
                is Resource.Error -> {
                    val errorMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = "assistant",
                        content = "Üzgünüm, bir hata oluştu: ${result.message}"
                    )
                    _state.value = _state.value.copy(
                        messages = _state.value.messages + errorMessage,
                        isTyping = false
                    )
                }
                else -> {}
            }
        }
    }
}
