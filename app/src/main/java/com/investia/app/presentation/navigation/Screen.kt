package com.investia.app.presentation.navigation

sealed class Screen(val route: String) {
    // Main tabs
    data object Dashboard : Screen("dashboard")
    data object DailyPicks : Screen("daily_picks")
    data object Portfolio : Screen("portfolio")
    data object Screener : Screen("screener")
    data object Profile : Screen("profile")

    // Sub screens
    data object StockDetail : Screen("stock_detail/{symbol}") {
        fun createRoute(symbol: String) = "stock_detail/$symbol"
    }
    data object SignalCenter : Screen("signal_center")
    data object News : Screen("news")
    data object Calculator : Screen("calculator")
    data object AIChat : Screen("ai_chat")
    data object Notifications : Screen("notifications")
    data object IPO : Screen("ipo")
    data object Alerts : Screen("alerts")
    data object Backtest : Screen("backtest")
    data object Performance : Screen("performance")
    data object ChatRooms : Screen("chat_rooms")
    data object ChatRoomDetail : Screen("chat_rooms/{roomId}") {
        fun createRoute(roomId: String) = "chat_rooms/$roomId"
    }

    // Auth
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
}
