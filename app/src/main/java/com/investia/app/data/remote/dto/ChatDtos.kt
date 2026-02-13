package com.investia.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.investia.app.domain.model.*

// ===== Chat (AI) =====
data class ChatRequestDto(
    val message: String,
    @SerializedName("chat_history") val chatHistory: List<ChatMessageDto> = emptyList()
)

data class ChatMessageDto(
    val role: String,
    val content: String
)

// Backend /api/ai/chat returns: { success, response: { id, role, content, timestamp } }
// We need a custom deserializer or use Any type and extract content
data class ChatResponseDto(
    val response: Any? = null,  // Can be String or Object with {id, role, content, timestamp}
    val success: Boolean = true
) {
    fun getResponseContent(): String {
        return when (response) {
            is String -> response
            is Map<*, *> -> (response as Map<*, *>)["content"]?.toString() ?: ""
            else -> response?.toString() ?: ""
        }
    }
}

// ===== Chat Room =====
data class ChatRoomListDto(
    val success: Boolean = true,
    val rooms: List<ChatRoomDto> = emptyList(),
    @SerializedName("total_online") val totalOnline: Int = 0
)

data class ChatRoomDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val category: String = "",
    @SerializedName("member_count") val memberCount: Int = 0,
    @SerializedName("is_active") val isActive: Boolean = true,
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
