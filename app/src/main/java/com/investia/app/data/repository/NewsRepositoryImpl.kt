package com.investia.app.data.repository

import com.investia.app.data.local.CacheManager
import com.investia.app.data.remote.InvestiaApiService
import com.investia.app.domain.model.NewsItem
import com.investia.app.domain.repository.NewsRepository
import com.investia.app.util.Resource
import com.investia.app.util.safeApiCall
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: InvestiaApiService,
    private val cache: CacheManager
) : NewsRepository {

    override suspend fun getNews(category: String): Resource<List<NewsItem>> {
        val cacheKey = CacheManager.keyNews(category)
        cache.get<List<NewsItem>>(cacheKey)?.let {
            return Resource.Success(it)
        }

        return safeApiCall {
            api.getNews(category).getAllArticles().map { it.toDomain() }
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { cache.put(cacheKey, it, CacheManager.TTL_NEWS) }
            }
        }
    }
}
