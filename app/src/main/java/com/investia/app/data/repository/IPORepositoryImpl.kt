package com.investia.app.data.repository

import com.investia.app.data.local.CacheManager
import com.investia.app.data.remote.InvestiaApiService
import com.investia.app.domain.model.*
import com.investia.app.domain.repository.IPORepository
import com.investia.app.util.Resource
import com.investia.app.util.safeApiCall
import javax.inject.Inject

class IPORepositoryImpl @Inject constructor(
    private val api: InvestiaApiService,
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
