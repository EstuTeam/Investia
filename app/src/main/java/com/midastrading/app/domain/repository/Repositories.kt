package com.midastrading.app.domain.repository

import com.midastrading.app.domain.model.*
import com.midastrading.app.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Market data: overview, stock quotes, daily picks, signals, screener
 */
interface MarketRepository {
    suspend fun getMarketOverview(): Resource<MarketOverview>
    suspend fun getStockQuote(symbol: String): Resource<StockQuote>
    suspend fun getDailyPicks(strategy: String = "hybrid"): Resource<DailyPicksResponse>
    suspend fun refreshDailyPicks(): Resource<DailyPicksResponse>
    suspend fun getStockSignals(symbol: String): Resource<SignalData>
    suspend fun getScreener(): Resource<List<StockPick>>
    suspend fun getMarketIndex(symbol: String, displayName: String): Resource<MarketIndex>
}

/**
 * News: economy, general, finance articles
 */
interface NewsRepository {
    suspend fun getNews(category: String = "economy"): Resource<List<NewsItem>>
}

/**
 * AI Chat: send messages, get market summary
 */
interface AIChatRepository {
    suspend fun sendChatMessage(message: String, history: List<ChatMessage>): Resource<String>
    suspend fun getMarketSummary(): Resource<String>
    suspend fun getAIStockAnalysis(symbol: String): Resource<String>
}

/**
 * Backtest: strategy testing
 */
interface BacktestRepository {
    suspend fun runBacktest(days: Int): Resource<BacktestResult>
    suspend fun quickBacktest(): Resource<BacktestResult>
}

/**
 * Advanced indicators: Ichimoku, Fibonacci, Bollinger
 */
interface IndicatorRepository {
    suspend fun getIchimoku(symbol: String): Resource<IchimokuData>
    suspend fun getFibonacci(symbol: String): Resource<FibonacciData>
    suspend fun getBollinger(symbol: String): Resource<BollingerData>
}

/**
 * Chat rooms: community trading chat
 */
interface ChatRoomRepository {
    suspend fun getChatRooms(): Resource<List<ChatRoom>>
    suspend fun getRoomMessages(roomId: String): Resource<List<RoomMessage>>
    suspend fun sendRoomMessage(roomId: String, content: String): Resource<Boolean>
}

/**
 * Auth: login, register, logout
 */
interface AuthRepository {
    val isLoggedIn: Flow<Boolean>
    val currentUserEmail: Flow<String?>
    val currentUserName: Flow<String?>
    suspend fun login(email: String, password: String): Resource<AuthResponse>
    suspend fun register(email: String, password: String, fullName: String): Resource<AuthResponse>
    suspend fun logout()
}

/**
 * IPO: halka arz takibi
 */
interface IPORepository {
    suspend fun getIPOList(status: String? = null): Resource<List<IPOItem>>
    suspend fun getIPOStats(): Resource<IPOStats>
    suspend fun getActiveIPOs(): Resource<List<IPOItem>>
    suspend fun getUpcomingIPOs(): Resource<List<IPOItem>>
}

/**
 * Portfolio: portföy yönetimi
 */
interface PortfolioRepository {
    suspend fun getPortfolios(): Resource<List<Portfolio>>
    suspend fun createPortfolio(name: String, description: String?): Resource<Portfolio>
    suspend fun deletePortfolio(id: Int): Resource<Boolean>
    suspend fun addTransaction(portfolioId: Int, symbol: String, type: String, quantity: Double, price: Double): Resource<Boolean>
    suspend fun deleteTransaction(portfolioId: Int, transactionId: Int): Resource<Boolean>
    suspend fun getWatchlists(): Resource<List<WatchlistItem>>
    suspend fun createWatchlist(name: String, tickers: List<String>): Resource<WatchlistItem>
    suspend fun addToWatchlist(watchlistId: Int, ticker: String): Resource<Boolean>
    suspend fun removeFromWatchlist(watchlistId: Int, ticker: String): Resource<Boolean>
}

/**
 * Alerts: fiyat alarmları
 */
interface AlertRepository {
    suspend fun getActiveAlerts(): Resource<List<AlertItem>>
    suspend fun createAlert(symbol: String, type: String, targetPrice: Double): Resource<Boolean>
    suspend fun deleteAlert(id: String): Resource<Boolean>
    suspend fun toggleAlert(id: String): Resource<Boolean>
    suspend fun checkTriggeredAlerts(): Resource<List<AlertItem>>
    suspend fun getNotificationHistory(): Resource<List<NotificationItem>>
    suspend fun markNotificationRead(id: String): Resource<Boolean>
    suspend fun markAllRead(): Resource<Boolean>
}
