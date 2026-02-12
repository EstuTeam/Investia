package com.investia.app.data.repository

import com.investia.app.data.local.CacheManager
import com.investia.app.data.remote.InvestiaApiService
import com.investia.app.data.remote.dto.*
import com.investia.app.domain.model.*
import com.investia.app.domain.repository.PortfolioRepository
import com.investia.app.util.Resource
import com.investia.app.util.safeApiCall
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    private val api: InvestiaApiService,
    private val cache: CacheManager
) : PortfolioRepository {

    override suspend fun getPortfolios(): Resource<List<Portfolio>> {
        cache.get<List<Portfolio>>(CacheManager.KEY_PORTFOLIO)?.let { return Resource.Success(it) }
        return safeApiCall {
            api.getPortfolios().data.map { it.toDomain() }
        }.also { result ->
            if (result is Resource.Success) result.data?.let { cache.put(CacheManager.KEY_PORTFOLIO, it, CacheManager.TTL_PORTFOLIO) }
        }
    }

    override suspend fun createPortfolio(name: String, description: String?): Resource<Portfolio> {
        cache.invalidate(CacheManager.KEY_PORTFOLIO)
        return safeApiCall { api.createPortfolio(CreatePortfolioDto(name, description)).toDomain() }
    }

    override suspend fun deletePortfolio(id: Int): Resource<Boolean> {
        cache.invalidate(CacheManager.KEY_PORTFOLIO)
        return safeApiCall { api.deletePortfolio(id).success }
    }

    override suspend fun addTransaction(
        portfolioId: Int, symbol: String, type: String, quantity: Double, price: Double
    ): Resource<Boolean> {
        cache.invalidate(CacheManager.KEY_PORTFOLIO)
        return safeApiCall {
            api.addTransaction(portfolioId, AddTransactionDto(symbol, type, quantity, price)).success
        }
    }

    override suspend fun deleteTransaction(portfolioId: Int, transactionId: Int): Resource<Boolean> {
        cache.invalidate(CacheManager.KEY_PORTFOLIO)
        return safeApiCall { api.deleteTransaction(portfolioId, transactionId).success }
    }

    override suspend fun getWatchlists(): Resource<List<WatchlistItem>> {
        return safeApiCall { api.getWatchlists().data.map { it.toDomain() } }
    }

    override suspend fun createWatchlist(name: String, tickers: List<String>): Resource<WatchlistItem> {
        return safeApiCall { api.createWatchlist(CreateWatchlistDto(name, tickers)).toDomain() }
    }

    override suspend fun addToWatchlist(watchlistId: Int, ticker: String): Resource<Boolean> {
        return safeApiCall { api.addToWatchlist(watchlistId, mapOf("ticker" to ticker)).success }
    }

    override suspend fun removeFromWatchlist(watchlistId: Int, ticker: String): Resource<Boolean> {
        return safeApiCall { api.removeFromWatchlist(watchlistId, ticker).success }
    }
}
