package com.midastrading.app.data.repository

import com.midastrading.app.data.local.CacheManager
import com.midastrading.app.data.remote.MidasApiService
import com.midastrading.app.data.remote.dto.SendRoomMessageDto
import com.midastrading.app.domain.model.ChatRoom
import com.midastrading.app.domain.model.RoomMessage
import com.midastrading.app.domain.repository.ChatRoomRepository
import com.midastrading.app.util.Resource
import com.midastrading.app.util.safeApiCall
import javax.inject.Inject

class ChatRoomRepositoryImpl @Inject constructor(
    private val api: MidasApiService,
    private val cache: CacheManager
) : ChatRoomRepository {

    override suspend fun getChatRooms(): Resource<List<ChatRoom>> {
        cache.get<List<ChatRoom>>(CacheManager.KEY_CHAT_ROOMS)?.let { return Resource.Success(it) }
        return safeApiCall { api.getChatRooms().rooms.map { it.toDomain() } }.also { result ->
            if (result is Resource.Success) result.data?.let {
                cache.put(CacheManager.KEY_CHAT_ROOMS, it, CacheManager.TTL_CHAT_ROOMS)
            }
        }
    }

    override suspend fun getRoomMessages(roomId: String): Resource<List<RoomMessage>> {
        return safeApiCall { api.getRoomMessages(roomId).messages.map { it.toDomain() } }
    }

    override suspend fun sendRoomMessage(roomId: String, content: String): Resource<Boolean> {
        return safeApiCall {
            api.sendRoomMessage(roomId, SendRoomMessageDto(content)).success
        }
    }
}
