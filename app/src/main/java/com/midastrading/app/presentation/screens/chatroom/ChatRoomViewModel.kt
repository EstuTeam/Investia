package com.midastrading.app.presentation.screens.chatroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midastrading.app.domain.model.ChatRoom
import com.midastrading.app.domain.model.RoomMessage
import com.midastrading.app.domain.repository.ChatRoomRepository
import com.midastrading.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatRoomListState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val rooms: List<ChatRoom> = emptyList()
)

data class ChatRoomDetailState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val messages: List<RoomMessage> = emptyList(),
    val roomId: String = "",
    val roomName: String = "",
    val isSending: Boolean = false
)

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRoomRepository: ChatRoomRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(ChatRoomListState())
    val listState: StateFlow<ChatRoomListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ChatRoomDetailState())
    val detailState: StateFlow<ChatRoomDetailState> = _detailState.asStateFlow()

    init {
        loadRooms()
    }

    fun loadRooms() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            val result = chatRoomRepository.getChatRooms()
            _listState.value = _listState.value.copy(
                isLoading = false,
                rooms = (result as? Resource.Success)?.data ?: emptyList(),
                error = (result as? Resource.Error)?.message
            )
        }
    }

    fun loadMessages(roomId: String, roomName: String = "") {
        _detailState.value = _detailState.value.copy(roomId = roomId, roomName = roomName)
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true)
            val result = chatRoomRepository.getRoomMessages(roomId)
            _detailState.value = _detailState.value.copy(
                isLoading = false,
                messages = (result as? Resource.Success)?.data ?: emptyList(),
                error = (result as? Resource.Error)?.message
            )
        }
    }

    fun sendMessage(content: String) {
        val roomId = _detailState.value.roomId
        if (roomId.isBlank() || content.isBlank()) return

        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isSending = true)
            chatRoomRepository.sendRoomMessage(roomId, content)
            _detailState.value = _detailState.value.copy(isSending = false)
            loadMessages(roomId)
        }
    }

    fun refreshMessages() {
        val roomId = _detailState.value.roomId
        if (roomId.isNotBlank()) loadMessages(roomId)
    }
}
