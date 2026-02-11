package com.midastrading.app.data.repository

import com.midastrading.app.data.local.CacheManager
import com.midastrading.app.data.remote.MidasApiService
import com.midastrading.app.domain.model.BollingerData
import com.midastrading.app.domain.model.FibonacciData
import com.midastrading.app.domain.model.IchimokuData
import com.midastrading.app.domain.repository.IndicatorRepository
import com.midastrading.app.util.Resource
import com.midastrading.app.util.safeApiCall
import javax.inject.Inject

class IndicatorRepositoryImpl @Inject constructor(
    private val api: MidasApiService,
    private val cache: CacheManager
) : IndicatorRepository {

    override suspend fun getIchimoku(symbol: String): Resource<IchimokuData> {
        val key = CacheManager.keyIndicator(symbol, "ichimoku")
        cache.get<IchimokuData>(key)?.let { return Resource.Success(it) }
        return safeApiCall { api.getIchimoku(symbol).toDomain() }.also { result ->
            if (result is Resource.Success) result.data?.let {
                cache.put(key, it, CacheManager.TTL_INDICATORS)
            }
        }
    }

    override suspend fun getFibonacci(symbol: String): Resource<FibonacciData> {
        val key = CacheManager.keyIndicator(symbol, "fibonacci")
        cache.get<FibonacciData>(key)?.let { return Resource.Success(it) }
        return safeApiCall { api.getFibonacci(symbol).toDomain() }.also { result ->
            if (result is Resource.Success) result.data?.let {
                cache.put(key, it, CacheManager.TTL_INDICATORS)
            }
        }
    }

    override suspend fun getBollinger(symbol: String): Resource<BollingerData> {
        val key = CacheManager.keyIndicator(symbol, "bollinger")
        cache.get<BollingerData>(key)?.let { return Resource.Success(it) }
        return safeApiCall { api.getBollinger(symbol).toDomain() }.also { result ->
            if (result is Resource.Success) result.data?.let {
                cache.put(key, it, CacheManager.TTL_INDICATORS)
            }
        }
    }
}
