package com.midastrading.app.data.repository

import com.midastrading.app.data.local.CacheManager
import com.midastrading.app.data.remote.MidasApiService
import com.midastrading.app.domain.model.*
import com.midastrading.app.domain.repository.IPORepository
import com.midastrading.app.util.Resource
import com.midastrading.app.util.safeApiCall
import javax.inject.Inject

class IPORepositoryImpl @Inject constructor(
    private val api: MidasApiService,
    private val cache: CacheManager
) : IPORepository {

    override suspend fun getIPOList(status: String?): Resource<List<IPOItem>> {
        val cacheKey = "${CacheManager.KEY_IPO_LIST}_${status ?: "all"}"
        cache.get<List<IPOItem>>(cacheKey)?.let { return Resource.Success(it) }
        return safeApiCall {
            api.getIPOList(status).ipos.map { it.toDomain() }
        }.also { result ->
            if (result is Resource.Success) result.data?.let { cache.put(cacheKey, it, CacheManager.TTL_IPO) }
        }
    }

    override suspend fun getIPOStats(): Resource<IPOStats> {
        cache.get<IPOStats>(CacheManager.KEY_IPO_STATS)?.let { return Resource.Success(it) }
        return safeApiCall { api.getIPOStats().toDomain() }.also { result ->
            if (result is Resource.Success) result.data?.let { cache.put(CacheManager.KEY_IPO_STATS, it, CacheManager.TTL_IPO) }
        }
    }

    override suspend fun getActiveIPOs(): Resource<List<IPOItem>> {
        return safeApiCall { api.getActiveIPOs().ipos.map { it.toDomain() } }
    }

    override suspend fun getUpcomingIPOs(): Resource<List<IPOItem>> {
        return safeApiCall { api.getUpcomingIPOs().ipos.map { it.toDomain() } }
    }
}
