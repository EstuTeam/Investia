package com.investia.app.data.remote

import com.investia.app.data.remote.dto.*
import retrofit2.http.*

interface InvestiaApiService {

    // ===== Market Data =====
    @GET("/api/market/all")
    suspend fun getMarketOverview(): MarketOverviewDto

    @GET("/api/stocks/{symbol}/data")
    suspend fun getStockQuote(@Path("symbol") symbol: String): StockQuoteDto

    @GET("/api/stocks/{symbol}/data")
    suspend fun getStockHistory(
        @Path("symbol") symbol: String,
        @Query("period") period: String = "6mo",
        @Query("interval") interval: String = "1d"
    ): StockHistoryDto

    // ===== Daily Picks =====
    @GET("/api/signals/daily-picks")
    suspend fun getDailyPicks(
        @Query("strategy") strategy: String = "hybrid",
        @Query("max_picks") maxPicks: Int = 5
    ): DailyPicksDto

    @GET("/api/signals/daily-picks")
    suspend fun refreshDailyPicks(
        @Query("force_refresh") forceRefresh: Boolean = true
    ): DailyPicksDto

    // ===== Signals =====
    @GET("/api/signals/{symbol}")
    suspend fun getStockSignals(
        @Path("symbol") symbol: String,
        @Query("strategy") strategy: String = "hybrid"
    ): SignalDataDto

    // ===== Screener =====
    @GET("/api/screener/daily-picks")
    suspend fun getScreener(): ScreenerResponseDto

    @GET("/api/screener/scan")
    suspend fun getTopPicks(): TopPicksDto

    // ===== News =====
    @GET("/api/news/{category}")
    suspend fun getNews(
        @Path("category") category: String = "economy",
        @Query("limit") limit: Int = 20
    ): NewsResponseDto

    // ===== Auth =====
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): AuthResponseDto

    @GET("/api/auth/me")
    suspend fun getCurrentUser(): UserDto

    @POST("/api/auth/logout")
    suspend fun logout(): GenericResponseDto

    @POST("/api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordDto): GenericResponseDto

    @GET("/api/auth/verify")
    suspend fun verifyToken(): GenericResponseDto

    // ===== AI Chat =====
    @POST("/api/ai/chat")
    suspend fun sendChatMessage(@Body request: ChatRequestDto): ChatResponseDto

    // ===== Alerts =====
    @GET("/api/alerts/active")
    suspend fun getActiveAlerts(): AlertsResponseDto

    @POST("/api/alerts/create")
    suspend fun createAlert(@Body request: CreateAlertDto): AlertResponseDto

    // ===== Health =====
    @GET("/health")
    suspend fun healthCheck(): HealthDto

    // ===== IPO =====
    @GET("/api/ipo/")
    suspend fun getIPOList(
        @Query("status") status: String? = null
    ): IPOListDto

    @GET("/api/ipo/stats")
    suspend fun getIPOStats(): IPOStatsDto

    @GET("/api/ipo/active")
    suspend fun getActiveIPOs(): IPOListDto

    @GET("/api/ipo/upcoming")
    suspend fun getUpcomingIPOs(): IPOListDto

    // ===== Alerts Enhanced =====
    @DELETE("/api/alerts/{id}")
    suspend fun deleteAlert(@Path("id") id: String): GenericResponseDto

    @PUT("/api/alerts/{id}/toggle")
    suspend fun toggleAlert(@Path("id") id: String): ToggleAlertDto

    @GET("/api/alerts/check")
    suspend fun checkTriggeredAlerts(): AlertsResponseDto

    @GET("/api/alerts/history")
    suspend fun getNotificationHistory(): NotificationsDto

    @PUT("/api/alerts/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: String): GenericResponseDto

    @PUT("/api/alerts/notifications/read-all")
    suspend fun markAllNotificationsRead(): GenericResponseDto

    @GET("/api/alerts/statistics")
    suspend fun getAlertStats(): AlertStatsDto

    // ===== Portfolio Enhanced =====
    @GET("/api/portfolio/")
    suspend fun getPortfolios(): PortfolioListDto

    @POST("/api/portfolio/")
    suspend fun createPortfolio(@Body request: CreatePortfolioDto): PortfolioDto

    @GET("/api/portfolio/{id}")
    suspend fun getPortfolioById(@Path("id") id: Int): PortfolioDto

    @DELETE("/api/portfolio/{id}")
    suspend fun deletePortfolio(@Path("id") id: Int): GenericResponseDto

    @POST("/api/portfolio/{id}/transactions")
    suspend fun addTransaction(
        @Path("id") portfolioId: Int,
        @Body transaction: AddTransactionDto
    ): GenericResponseDto

    @DELETE("/api/portfolio/{id}/transactions/{tid}")
    suspend fun deleteTransaction(
        @Path("id") portfolioId: Int,
        @Path("tid") transactionId: Int
    ): GenericResponseDto

    @GET("/api/portfolio/watchlists/all")
    suspend fun getWatchlists(): WatchlistsResponseDto

    @POST("/api/portfolio/watchlists")
    suspend fun createWatchlist(@Body request: CreateWatchlistDto): WatchlistDto

    @POST("/api/portfolio/watchlists/{id}/add")
    suspend fun addToWatchlist(
        @Path("id") watchlistId: Int,
        @Body body: Map<String, String>
    ): GenericResponseDto

    @DELETE("/api/portfolio/watchlists/{id}/remove/{ticker}")
    suspend fun removeFromWatchlist(
        @Path("id") watchlistId: Int,
        @Path("ticker") ticker: String
    ): GenericResponseDto

    // ===== Backtest =====
    @GET("/api/backtest/daily-strategy")
    suspend fun runBacktest(@Query("days") days: Int): BacktestResultDto

    @GET("/api/backtest/quick-test")
    suspend fun quickBacktest(): BacktestResultDto

    // ===== Advanced Indicators =====
    @GET("/api/indicators/{symbol}/ichimoku")
    suspend fun getIchimoku(@Path("symbol") symbol: String): IchimokuDto

    @GET("/api/indicators/{symbol}/fibonacci")
    suspend fun getFibonacci(@Path("symbol") symbol: String): FibonacciDto

    @GET("/api/indicators/{symbol}/bollinger")
    suspend fun getBollinger(@Path("symbol") symbol: String): BollingerDto

    // ===== Screener Enhanced =====
    @GET("/api/screener/top-movers")
    suspend fun getTopMovers(): ScreenerResponseDto

    @GET("/api/market-status")
    suspend fun getMarketTiming(): GenericResponseDto

    // ===== Chat Rooms =====
    @GET("/api/chat/rooms")
    suspend fun getChatRooms(): ChatRoomListDto

    @GET("/api/chat/rooms/{id}/messages")
    suspend fun getRoomMessages(
        @Path("id") roomId: String,
        @Query("limit") limit: Int = 50
    ): RoomMessagesDto

    @POST("/api/chat/rooms/{id}/messages")
    suspend fun sendRoomMessage(
        @Path("id") roomId: String,
        @Body message: SendRoomMessageDto
    ): GenericResponseDto

    @POST("/api/chat/rooms/{id}/join")
    suspend fun joinRoom(@Path("id") roomId: String): GenericResponseDto

    // ===== AI Enhanced =====
    @GET("/api/ai/market-summary")
    suspend fun getMarketSummary(): ChatResponseDto

    @GET("/api/ai/analysis/{symbol}")
    suspend fun getAIStockAnalysis(@Path("symbol") symbol: String): ChatResponseDto
}
