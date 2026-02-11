package com.midastrading.app.data.repository

import com.midastrading.app.data.remote.MidasApiService
import com.midastrading.app.data.remote.dto.ChatMessageDto
import com.midastrading.app.data.remote.dto.ChatRequestDto
import com.midastrading.app.domain.model.ChatMessage
import com.midastrading.app.domain.repository.AIChatRepository
import com.midastrading.app.util.Resource
import com.midastrading.app.util.safeApiCall
import javax.inject.Inject

class AIChatRepositoryImpl @Inject constructor(
    private val api: MidasApiService
) : AIChatRepository {

    override suspend fun sendChatMessage(
        message: String,
        history: List<ChatMessage>
    ): Resource<String> {
        return safeApiCall {
            val request = ChatRequestDto(
                message = message,
                chatHistory = history.map { ChatMessageDto(it.role, it.content) }
            )
            val response = api.sendChatMessage(request)
            if (response.success) {
                response.response
            } else {
                throw Exception("AI yanıt veremedi")
            }
        }
    }

    override suspend fun getMarketSummary(): Resource<String> {
        return safeApiCall {
            api.getMarketSummary().response
        }
    }

    override suspend fun getAIStockAnalysis(symbol: String): Resource<String> {
        return safeApiCall {
            api.getAIStockAnalysis(symbol).response
        }
    }
}
