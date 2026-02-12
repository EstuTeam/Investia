package com.investia.app.data.repository

import com.investia.app.data.remote.InvestiaApiService
import com.investia.app.data.remote.dto.ChatMessageDto
import com.investia.app.data.remote.dto.ChatRequestDto
import com.investia.app.domain.model.ChatMessage
import com.investia.app.domain.repository.AIChatRepository
import com.investia.app.util.Resource
import com.investia.app.util.safeApiCall
import javax.inject.Inject

class AIChatRepositoryImpl @Inject constructor(
    private val api: InvestiaApiService
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
                response.getResponseContent()
            } else {
                throw Exception("AI yanıt veremedi")
            }
        }
    }

    override suspend fun getMarketSummary(): Resource<String> {
        return safeApiCall {
            api.getMarketSummary().getResponseContent()
        }
    }

    override suspend fun getAIStockAnalysis(symbol: String): Resource<String> {
        return safeApiCall {
            api.getAIStockAnalysis(symbol).getResponseContent()
        }
    }
}
