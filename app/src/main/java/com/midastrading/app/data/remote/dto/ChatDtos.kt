package com.midastrading.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.midastrading.app.domain.model.*

// ===== Chat (AI) =====
data class ChatRequestDto(
    val message: String,
    @SerializedName("chat_history") val chatHistory: List<ChatMessageDto> = emptyList()
)

data class ChatMessageDto(
    val role: String,
    val content: String
)

data class ChatResponseDto(
    val response: String = "",
    val success: Boolean = true
)

// ===== Chat Room =====
data class ChatRoomListDto(
    val rooms: List<ChatRoomDto> = emptyList()
)

data class ChatRoomDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    @SerializedName("member_count") val memberCount: Int = 0,
    @SerializedName("last_message") val lastMessage: String = "",
    @SerializedName("last_message_time") val lastMessageTime: String = ""
) {
    fun toDomain() = ChatRoom(id, name, description, memberCount, lastMessage, lastMessageTime)
}

data class RoomMessagesDto(
    val messages: List<RoomMessageDto> = emptyList()
)

data class RoomMessageDto(
    val id: String = "",
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("user_name") val userName: String = "",
    val content: String = "",
    val timestamp: String = "",
    val reactions: Map<String, Int> = emptyMap()
) {
    fun toDomain() = RoomMessage(id, userId, userName, content, timestamp, reactions)
}

data class SendRoomMessageDto(
    val content: String
)
