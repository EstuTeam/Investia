package com.midastrading.app.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.midastrading.app.domain.repository.AuthRepository
import com.midastrading.app.presentation.screens.alerts.AlertScreen
import com.midastrading.app.presentation.screens.auth.ForgotPasswordScreen
import com.midastrading.app.presentation.screens.auth.LoginScreen
import com.midastrading.app.presentation.screens.auth.RegisterScreen
import com.midastrading.app.presentation.screens.backtest.BacktestScreen
import com.midastrading.app.presentation.screens.calculator.CalculatorScreen
import com.midastrading.app.presentation.screens.chat.AIChatScreen
import com.midastrading.app.presentation.screens.chatroom.ChatRoomDetailScreen
import com.midastrading.app.presentation.screens.chatroom.ChatRoomListScreen
import com.midastrading.app.presentation.screens.dailypicks.DailyPicksScreen
import com.midastrading.app.presentation.screens.dashboard.DashboardScreen
import com.midastrading.app.presentation.screens.ipo.IPOScreen
import com.midastrading.app.presentation.screens.news.NewsScreen
import com.midastrading.app.presentation.screens.notifications.NotificationsScreen
import com.midastrading.app.presentation.screens.performance.PerformanceScreen
import com.midastrading.app.presentation.screens.portfolio.PortfolioScreen
import com.midastrading.app.presentation.screens.profile.ProfileScreen
import com.midastrading.app.presentation.screens.screener.ScreenerScreen
import com.midastrading.app.presentation.screens.signals.SignalCenterScreen
import com.midastrading.app.presentation.screens.stockdetail.StockDetailScreen

/**
 * Composable that requires authentication. If user is not logged in,
 * redirects to login screen.
 */
@Composable
private fun AuthGuard(
    isLoggedIn: Boolean,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    if (isLoggedIn) {
        content()
    } else {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Dashboard.route) { inclusive = false }
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun MidasNavHost(
    authRepository: AuthRepository = hiltViewModel<AuthGatingViewModel>().authRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isLoggedIn by authRepository.isLoggedIn.collectAsState(initial = false)

    val bottomBarScreens = listOf(
        Screen.Dashboard.route,
        Screen.DailyPicks.route,
        Screen.IPO.route,
        Screen.Screener.route,
        Screen.Profile.route
    )

    val showBottomBar = currentRoute in bottomBarScreens

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                MidasBottomBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(200)) + slideInHorizontally(
                    initialOffsetX = { 100 },
                    animationSpec = tween(200)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            }
        ) {
            // Main tabs (public)
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController = navController)
            }
            composable(Screen.DailyPicks.route) {
                DailyPicksScreen(navController = navController)
            }
            composable(Screen.Screener.route) {
                ScreenerScreen(navController = navController)
            }

            // Auth-protected tabs
            composable(Screen.Portfolio.route) {
                AuthGuard(isLoggedIn, navController) {
                    PortfolioScreen(navController = navController)
                }
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }

            // Sub screens (public)
            composable(
                route = Screen.StockDetail.route,
                arguments = listOf(navArgument("symbol") { type = NavType.StringType })
            ) { entry ->
                val symbol = entry.arguments?.getString("symbol") ?: ""
                StockDetailScreen(symbol = symbol, navController = navController)
            }
            composable(Screen.SignalCenter.route) {
                SignalCenterScreen(navController = navController)
            }
            composable(Screen.News.route) {
                NewsScreen(navController = navController)
            }
            composable(Screen.Calculator.route) {
                CalculatorScreen(navController = navController)
            }
            composable(Screen.IPO.route) {
                IPOScreen(navController = navController)
            }
            composable(Screen.Backtest.route) {
                BacktestScreen(navController = navController)
            }

            // Auth-protected sub screens
            composable(Screen.AIChat.route) {
                AuthGuard(isLoggedIn, navController) {
                    AIChatScreen(navController = navController)
                }
            }
            composable(Screen.Alerts.route) {
                AuthGuard(isLoggedIn, navController) {
                    AlertScreen(navController = navController)
                }
            }
            composable(Screen.Performance.route) {
                AuthGuard(isLoggedIn, navController) {
                    PerformanceScreen(navController = navController)
                }
            }
            composable(Screen.ChatRooms.route) {
                AuthGuard(isLoggedIn, navController) {
                    ChatRoomListScreen(navController = navController)
                }
            }
            composable(
                route = Screen.ChatRoomDetail.route,
                arguments = listOf(navArgument("roomId") { type = NavType.StringType })
            ) { entry ->
                val roomId = entry.arguments?.getString("roomId") ?: ""
                AuthGuard(isLoggedIn, navController) {
                    ChatRoomDetailScreen(roomId = roomId, navController = navController)
                }
            }

            // Auth-protected: Notifications
            composable(Screen.Notifications.route) {
                AuthGuard(isLoggedIn, navController) {
                    NotificationsScreen(navController = navController)
                }
            }

            // Auth screens
            composable(Screen.Login.route) {
                LoginScreen(navController = navController)
            }
            composable(Screen.Register.route) {
                RegisterScreen(navController = navController)
            }
            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(navController = navController)
            }
        }
    }
}
