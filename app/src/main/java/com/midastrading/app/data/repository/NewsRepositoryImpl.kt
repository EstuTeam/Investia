package com.midastrading.app.data.repository

import com.midastrading.app.data.local.CacheManager
import com.midastrading.app.data.remote.MidasApiService
import com.midastrading.app.domain.model.NewsItem
import com.midastrading.app.domain.repository.NewsRepository
import com.midastrading.app.util.Resource
import com.midastrading.app.util.safeApiCall
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: MidasApiService,
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
