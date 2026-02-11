package com.midastrading.app.data.repository

import com.midastrading.app.data.local.CacheManager
import com.midastrading.app.data.local.CacheManager.Companion.KEY_DAILY_PICKS
import com.midastrading.app.data.local.CacheManager.Companion.KEY_MARKET_OVERVIEW
import com.midastrading.app.data.local.CacheManager.Companion.KEY_SCREENER
import com.midastrading.app.data.local.CacheManager.Companion.TTL_DAILY_PICKS
import com.midastrading.app.data.local.CacheManager.Companion.TTL_MARKET_OVERVIEW
import com.midastrading.app.data.local.CacheManager.Companion.TTL_SCREENER
import com.midastrading.app.data.local.CacheManager.Companion.TTL_STOCK_QUOTE
import com.midastrading.app.data.local.CacheManager.Companion.TTL_STOCK_SIGNALS
import com.midastrading.app.data.remote.MidasApiService
import com.midastrading.app.domain.model.*
import com.midastrading.app.domain.repository.MarketRepository
import com.midastrading.app.util.Resource
import com.midastrading.app.util.safeApiCall
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val api: MidasApiService,
    private val cache: CacheManager
) : MarketRepository {

    override suspend fun getMarketOverview(): Resource<MarketOverview> {
        cache.get<MarketOverview>(KEY_MARKET_OVERVIEW)?.let {
            return Resource.Success(it)
        }

        return safeApiCall {
            api.getMarketOverview().toDomain()
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { cache.put(KEY_MARKET_OVERVIEW, it, TTL_MARKET_OVERVIEW) }
            }
        }
    }

    override suspend fun getStockQuote(symbol: String): Resource<StockQuote> {
        val cacheKey = CacheManager.keyStockQuote(symbol)
        cache.get<StockQuote>(cacheKey)?.let {
            return Resource.Success(it)
        }

        return safeApiCall {
            val history = api.getStockHistory(symbol)
            val allPrices = history.getAllPrices()
            val latest = allPrices.lastOrNull()
            val previous = if (allPrices.size >= 2) allPrices[allPrices.size - 2] else null
            val price = latest?.close ?: 0.0
            val prevClose = previous?.close ?: price
            val change = price - prevClose
            val changePct = if (prevClose != 0.0) (change / prevClose) * 100 else 0.0

            StockQuote(
                symbol = history.symbol.removeSuffix(".IS"),
                name = history.symbol.removeSuffix(".IS"),
                price = price,
                previousClose = prevClose,
                change = change,
                changePercent = changePct,
                volume = latest?.volume ?: 0L,
                marketCap = 0L,
                dayHigh = latest?.high ?: 0.0,
                dayLow = latest?.low ?: 0.0
            )
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { cache.put(cacheKey, it, TTL_STOCK_QUOTE) }
            }
        }
    }

    override suspend fun getMarketIndex(symbol: String, displayName: String): Resource<MarketIndex> {
        val cacheKey = "index_$symbol"
        cache.get<MarketIndex>(cacheKey)?.let {
            return Resource.Success(it)
        }

        return safeApiCall {
            val history = api.getStockHistory(symbol, "5d", "1d")
            val prices = history.getAllPrices()
            val latest = prices.lastOrNull()
            val prev = if (prices.size >= 2) prices[prices.size - 2] else null
            val price = latest?.close ?: 0.0
            val prevClose = prev?.close ?: price
            val change = price - prevClose
            val changePct = if (prevClose != 0.0) (change / prevClose) * 100 else 0.0
            MarketIndex(
                name = displayName,
                symbol = symbol,
                price = price,
                change = change,
                changePercent = changePct
            )
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { cache.put(cacheKey, it, TTL_STOCK_QUOTE) }
            }
        }
    }

    override suspend fun getDailyPicks(strategy: String): Resource<DailyPicksResponse> {
        val cacheKey = "${KEY_DAILY_PICKS}_$strategy"
        cache.get<DailyPicksResponse>(cacheKey)?.let {
            return Resource.Success(it)
        }

        return safeApiCall {
            api.getDailyPicks(strategy).toDomain()
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { cache.put(cacheKey, it, TTL_DAILY_PICKS) }
            }
        }
    }

    override suspend fun refreshDailyPicks(): Resource<DailyPicksResponse> {
        cache.invalidatePrefix(KEY_DAILY_PICKS)

        return safeApiCall {
            api.refreshDailyPicks().toDomain()
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { cache.put(KEY_DAILY_PICKS, it, TTL_DAILY_PICKS) }
            }
        }
    }

    override suspend fun getStockSignals(symbol: String): Resource<SignalData> {
        val cacheKey = CacheManager.keyStockSignals(symbol)
        cache.get<SignalData>(cacheKey)?.let {
            return Resource.Success(it)
        }

        return safeApiCall {
            api.getStockSignals(symbol).toDomain()
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { cache.put(cacheKey, it, TTL_STOCK_SIGNALS) }
            }
        }
    }

    override suspend fun getScreener(): Resource<List<StockPick>> {
        cache.get<List<StockPick>>(KEY_SCREENER)?.let {
            return Resource.Success(it)
        }

        return safeApiCall {
            val response = api.getTopMovers()
            val fromGainers = response.gainers.map { it.toDomain() }
            val fromLosers = response.losers.map { it.toDomain() }
            // Fallback to old format if top-movers returns empty
            if (fromGainers.isEmpty() && fromLosers.isEmpty()) {
                val old = response.picks.map { it.toDomain() }
                    .ifEmpty { response.opportunities.map { it.toDomain() } }
                    .ifEmpty { response.stocks.map { it.toDomain() } }
                old
            } else {
                fromGainers + fromLosers
            }
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { cache.put(KEY_SCREENER, it, TTL_SCREENER) }
            }
        }
    }
}
