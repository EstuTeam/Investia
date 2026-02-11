package com.midastrading.app.domain.model

data class StockPick(
    val symbol: String,
    val name: String = "",
    val score: Int,
    val price: Double,
    val changePercent: Double = 0.0,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double = 0.0,
    val riskPercent: Double,
    val rsi: Double = 0.0,
    val signal: SignalType = SignalType.HOLD,
    val reasons: List<String> = emptyList(),
    val sector: String = ""
)

data class StockQuote(
    val symbol: String,
    val name: String,
    val price: Double,
    val previousClose: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,
    val marketCap: Long = 0,
    val dayHigh: Double = 0.0,
    val dayLow: Double = 0.0
)

data class DailyPicksResponse(
    val picks: List<StockPick>,
    val strategy: String,
    val timestamp: String,
    val marketStatus: String = "open"
)

data class PortfolioItem(
    val symbol: String,
    val name: String,
    val quantity: Double,
    val avgCost: Double,
    val currentPrice: Double,
    val totalCost: Double,
    val totalValue: Double,
    val profitLoss: Double,
    val profitLossPercent: Double
)

data class PortfolioSummary(
    val totalValue: Double,
    val totalCost: Double,
    val totalPnL: Double,
    val totalPnLPercent: Double,
    val dailyPnL: Double,
    val dailyPnLPercent: Double,
    val items: List<PortfolioItem>
)

data class NewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val source: String,
    val url: String,
    val imageUrl: String = "",
    val publishedAt: String,
    val category: String = "general"
)

data class SignalData(
    val symbol: String,
    val signal: SignalType,
    val action: String,
    val price: Double,
    val changePercent: Double,
    val rsi: Double = 0.0,
    val macdSignal: String = "",
    val score: Int = 0
)

enum class SignalType {
    STRONG_BUY, BUY, HOLD, SELL, STRONG_SELL
}

data class MarketOverview(
    val stocks: List<MarketStock> = emptyList(),
    val summary: MarketSummary? = null,
    val timestamp: String = ""
)

data class MarketStock(
    val symbol: String,
    val name: String = "",
    val price: Double = 0.0,
    val change: Double = 0.0,
    val changePercent: Double = 0.0,
    val volume: Long = 0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val open: Double = 0.0
)

data class MarketSummary(
    val advancing: Int = 0,
    val declining: Int = 0,
    val avgChange: Double = 0.0,
    val totalVolume: Long = 0,
    val marketTrend: String = ""
)

data class IndexData(
    val value: Double,
    val change: Double,
    val changePercent: Double
)

data class MarketIndex(
    val name: String,
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double
)

data class ChatMessage(
    val id: String = "",
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

// ===== IPO =====
data class IPOItem(
    val symbol: String,
    val companyName: String,
    val sector: String = "",
    val status: IPOStatus = IPOStatus.UPCOMING,
    val startDate: String = "",
    val endDate: String = "",
    val price: Double = 0.0,
    val priceRange: String = "",
    val lotSize: Int = 0,
    val totalShares: Long = 0,
    val demandMultiple: Double = 0.0,
    val description: String = "",
    val currentPrice: Double = 0.0,
    val returnPercent: Double = 0.0
)

enum class IPOStatus {
    UPCOMING, ACTIVE, COMPLETED, TRADING
}

data class IPOStats(
    val totalIPOs: Int = 0,
    val activeIPOs: Int = 0,
    val upcomingIPOs: Int = 0,
    val avgReturn: Double = 0.0
)

// ===== Alerts =====
data class AlertItem(
    val id: String,
    val symbol: String,
    val type: String,
    val targetPrice: Double = 0.0,
    val currentPrice: Double = 0.0,
    val active: Boolean = true,
    val triggered: Boolean = false,
    val createdAt: String = ""
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val priority: String = "medium",
    val isRead: Boolean = false,
    val createdAt: String = ""
)

// ===== Portfolio Enhanced =====
data class Portfolio(
    val id: Int,
    val name: String,
    val description: String = "",
    val isDefault: Boolean = false,
    val holdings: List<PortfolioItem> = emptyList(),
    val totalValue: Double = 0.0,
    val totalCost: Double = 0.0
)

data class Transaction(
    val id: Int = 0,
    val portfolioId: Int = 0,
    val symbol: String,
    val type: TransactionType,
    val quantity: Double,
    val price: Double,
    val date: String = "",
    val notes: String = ""
)

enum class TransactionType {
    BUY, SELL, DIVIDEND, SPLIT
}

data class WatchlistItem(
    val id: Int,
    val name: String,
    val tickers: List<String> = emptyList()
)

// ===== Backtest =====
data class BacktestResult(
    val totalTrades: Int = 0,
    val winRate: Double = 0.0,
    val profitFactor: Double = 0.0,
    val totalReturn: Double = 0.0,
    val maxDrawdown: Double = 0.0,
    val sharpeRatio: Double = 0.0,
    val trades: List<BacktestTrade> = emptyList(),
    val equityCurve: List<Double> = emptyList()
)

data class BacktestTrade(
    val symbol: String,
    val entryDate: String = "",
    val exitDate: String = "",
    val entryPrice: Double = 0.0,
    val exitPrice: Double = 0.0,
    val quantity: Double = 0.0,
    val profitLoss: Double = 0.0,
    val profitLossPercent: Double = 0.0,
    val signal: String = ""
)

// ===== Performance =====
data class PerformanceData(
    val dailyPnL: Double = 0.0,
    val weeklyPnL: Double = 0.0,
    val monthlyPnL: Double = 0.0,
    val totalPnL: Double = 0.0,
    val winRate: Double = 0.0,
    val totalTrades: Int = 0,
    val trades: List<BacktestTrade> = emptyList()
)

// ===== Chat Room =====
data class ChatRoom(
    val id: String,
    val name: String,
    val description: String = "",
    val memberCount: Int = 0,
    val lastMessage: String = "",
    val lastMessageTime: String = ""
)

data class RoomMessage(
    val id: String,
    val userId: Int = 0,
    val userName: String = "",
    val content: String,
    val timestamp: String = "",
    val reactions: Map<String, Int> = emptyMap()
)

// ===== Advanced Indicators =====
data class IchimokuData(
    val tenkan: List<Double> = emptyList(),
    val kijun: List<Double> = emptyList(),
    val senkouA: List<Double> = emptyList(),
    val senkouB: List<Double> = emptyList(),
    val chikou: List<Double> = emptyList(),
    val signal: String = ""
)

data class FibonacciData(
    val levels: Map<String, Double> = emptyMap(),
    val trend: String = "",
    val currentPrice: Double = 0.0
)

data class BollingerData(
    val upper: List<Double> = emptyList(),
    val middle: List<Double> = emptyList(),
    val lower: List<Double> = emptyList(),
    val signal: String = ""
)
