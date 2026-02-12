package com.investia.app.data.local

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory cache with TTL support for market data.
 * Prevents excessive API calls and provides offline-like experience.
 */
@Singleton
class CacheManager @Inject constructor() {

    private data class CacheEntry<T>(
        val data: T,
        val timestamp: Long = System.currentTimeMillis(),
        val ttlMs: Long
    ) {
        fun isExpired(): Boolean =
            System.currentTimeMillis() - timestamp > ttlMs
    }

    private val cache = ConcurrentHashMap<String, CacheEntry<*>>()

    companion object {
        // Cache TTL constants
        const val TTL_MARKET_OVERVIEW = 60_000L      // 1 minute
        const val TTL_DAILY_PICKS = 300_000L          // 5 minutes
        const val TTL_SCREENER = 180_000L             // 3 minutes
        const val TTL_STOCK_QUOTE = 30_000L           // 30 seconds
        const val TTL_STOCK_SIGNALS = 120_000L        // 2 minutes
        const val TTL_NEWS = 600_000L                 // 10 minutes
        const val TTL_PORTFOLIO = 60_000L             // 1 minute
        const val TTL_IPO = 300_000L                  // 5 minutes
        const val TTL_ALERTS = 120_000L               // 2 minutes
        const val TTL_BACKTEST = 600_000L             // 10 minutes
        const val TTL_INDICATORS = 120_000L           // 2 minutes
        const val TTL_CHAT_ROOMS = 60_000L            // 1 minute
        const val TTL_PERFORMANCE = 300_000L          // 5 minutes

        // Cache keys
        const val KEY_MARKET_OVERVIEW = "market_overview"
        const val KEY_DAILY_PICKS = "daily_picks"
        const val KEY_SCREENER = "screener"
        const val KEY_NEWS = "news"
        const val KEY_PORTFOLIO = "portfolio"
        const val KEY_IPO_LIST = "ipo_list"
        const val KEY_IPO_STATS = "ipo_stats"
        const val KEY_ALERTS = "alerts"
        const val KEY_BACKTEST = "backtest"
        const val KEY_CHAT_ROOMS = "chat_rooms"
        const val KEY_PERFORMANCE = "performance"
        fun keyStockQuote(symbol: String) = "quote_$symbol"
        fun keyStockSignals(symbol: String) = "signals_$symbol"
        fun keyNews(category: String) = "news_$category"
        fun keyIndicator(symbol: String, type: String) = "indicator_${type}_$symbol"
    }

    /**
     * Get cached data if not expired
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? {
        val entry = cache[key] as? CacheEntry<T> ?: return null
        return if (entry.isExpired()) {
            cache.remove(key)
            null
        } else {
            entry.data
        }
    }

    /**
     * Put data into cache with specified TTL
     */
    fun <T : Any> put(key: String, data: T, ttlMs: Long) {
        cache[key] = CacheEntry(data, ttlMs = ttlMs)
    }

    /**
     * Invalidate a specific cache entry
     */
    fun invalidate(key: String) {
        cache.remove(key)
    }

    /**
     * Invalidate all entries matching a prefix
     */
    fun invalidatePrefix(prefix: String) {
        cache.keys.filter { it.startsWith(prefix) }.forEach { cache.remove(it) }
    }

    /**
     * Clear all cached data
     */
    fun clearAll() {
        cache.clear()
    }

    /**
     * Get cache statistics for debugging
     */
    fun getStats(): Map<String, Any> {
        val total = cache.size
        val expired = cache.values.count { it.isExpired() }
        return mapOf(
            "total" to total,
            "active" to (total - expired),
            "expired" to expired
        )
    }
}
