package com.investia.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.investia.app.domain.model.*

// ===== News =====
data class NewsResponseDto(
    val type: String = "",
    val turkey: List<NewsItemDto> = emptyList(),
    val world: List<NewsItemDto> = emptyList(),
    val finance: List<NewsItemDto> = emptyList(),
    val crypto: List<NewsItemDto> = emptyList(),
    val articles: List<NewsItemDto> = emptyList(),
    val source: String = ""
) {
    fun getAllArticles(): List<NewsItemDto> {
        // Combine ALL categories, not just the first non-empty one
        val combined = mutableListOf<NewsItemDto>()
        combined.addAll(turkey)
        combined.addAll(world)
        combined.addAll(finance)
        combined.addAll(crypto)
        combined.addAll(articles)
        return if (combined.isNotEmpty()) combined else articles
    }
}

data class NewsItemDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val summary: String = "",
    val source: String = "",
    val link: String = "",
    val url: String = "",
    @SerializedName("image_url") val imageUrl: String = "",
    val published: String = "",
    @SerializedName("published_at") val publishedAt: String = "",
    val category: String = "general",
    val timestamp: Double = 0.0
) {
    fun toDomain() = NewsItem(
        id = id.ifBlank { title.hashCode().toString() },
        title = title,
        summary = description.ifBlank { summary },
        source = source,
        url = link.ifBlank { url },
        imageUrl = imageUrl,
        publishedAt = published.ifBlank { publishedAt },
        category = category
    )
}
