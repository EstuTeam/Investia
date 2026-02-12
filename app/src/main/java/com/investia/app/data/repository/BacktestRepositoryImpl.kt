package com.investia.app.data.repository

import com.investia.app.data.local.CacheManager
import com.investia.app.data.remote.InvestiaApiService
import com.investia.app.domain.model.BacktestResult
import com.investia.app.domain.repository.BacktestRepository
import com.investia.app.util.Resource
import com.investia.app.util.safeApiCall
import javax.inject.Inject

class BacktestRepositoryImpl @Inject constructor(
    private val api: InvestiaApiService,
    private val cache: CacheManager
) : BacktestRepository {

    override suspend fun runBacktest(days: Int): Resource<BacktestResult> {
        val cacheKey = "${CacheManager.KEY_BACKTEST}_$days"
        cache.get<BacktestResult>(cacheKey)?.let { return Resource.Success(it) }
        return safeApiCall { api.runBacktest(days).toDomain() }.also { result ->
            if (result is Resource.Success) result.data?.let {
                cache.put(cacheKey, it, CacheManager.TTL_BACKTEST)
            }
        }
    }

    override suspend fun quickBacktest(): Resource<BacktestResult> {
        cache.get<BacktestResult>(CacheManager.KEY_BACKTEST)?.let { return Resource.Success(it) }
        return safeApiCall { api.quickBacktest().toDomain() }.also { result ->
            if (result is Resource.Success) result.data?.let {
                cache.put(CacheManager.KEY_BACKTEST, it, CacheManager.TTL_BACKTEST)
            }
        }
    }
}
