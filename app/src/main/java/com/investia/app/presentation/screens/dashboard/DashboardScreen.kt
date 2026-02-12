package com.investia.app.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.investia.app.domain.model.MarketIndex
import com.investia.app.domain.model.MarketOverview
import com.investia.app.presentation.components.*
import com.investia.app.presentation.navigation.Screen
import com.investia.app.presentation.theme.*
import com.investia.app.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
        // ===== Header =====
        item {
            DashboardHeader(
                onNotificationClick = { navController.navigate(Screen.Notifications.route) },
                onChatClick = { navController.navigate(Screen.AIChat.route) }
            )
        }

        // ===== Portfolio Summary Card =====
        item {
            PortfolioSummaryCard(
                summary = state.portfolioSummary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // ===== Market Indices =====
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Piyasa Özeti",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            if (state.marketIndices.isNotEmpty()) {
                MarketIndicesRow(indices = state.marketIndices)
            } else if (state.isLoading) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(4) {
                        ShimmerBox(
                            modifier = Modifier
                                .width(155.dp)
                                .height(110.dp)
                        )
                    }
                }
            }
        }

        // ===== Market Summary Bar =====
        item {
            state.marketOverview?.summary?.let { summary ->
                Spacer(modifier = Modifier.height(10.dp))
                MarketSummaryBar(summary)
            }
        }

        // ===== Quick Actions =====
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Hızlı İşlemler",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            QuickActionsGrid(navController = navController)
        }

        // ===== Top Picks =====
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Günün Fırsatları",
                action = "Tümünü Gör",
                onAction = { navController.navigate(Screen.DailyPicks.route) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (state.isLoading) {
            items(3) {
                ShimmerBox(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .height(160.dp),
                    cornerRadius = 20.dp
                )
            }
        } else if (state.topPicks.isNotEmpty()) {
            items(state.topPicks.take(3)) { pick ->
                StockPickCard(
                    pick = pick,
                    onClick = {
                        navController.navigate(Screen.StockDetail.createRoute(pick.symbol))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        } else {
            item {
                GlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(InvestiaPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.SearchOff, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Bugün fırsat bulunamadı",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "Piyasa koşulları uygun olduğunda fırsatlar burada görünecek",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // ===== Error =====
        if (state.error != null && state.topPicks.isEmpty() && state.marketIndices.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Filled.WifiOff, null, tint = WarningOrange, modifier = Modifier.size(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bağlantı hatası", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
                            Text(state.error ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { viewModel.loadDashboard() }) {
                            Text("Yenile", color = InvestiaPrimary)
                        }
                    }
                }
            }
        }
    }

        // Refresh indicator at top
        if (state.isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = InvestiaPrimary
            )
        }
    }
}

// ===== Header =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardHeader(
    onNotificationClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Merhaba 👋",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Investia",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onChatClick) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(InvestiaPrimary.copy(alpha = 0.12f))
                        .border(1.dp, InvestiaPrimary.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.SmartToy, "AI Asistan",
                        tint = InvestiaPrimary, modifier = Modifier.size(20.dp)
                    )
                }
            }
            IconButton(onClick = onNotificationClick) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Notifications, "Bildirimler",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ===== Portfolio Summary =====
@Composable
private fun PortfolioSummaryCard(
    summary: com.investia.app.domain.model.PortfolioSummary?,
    modifier: Modifier = Modifier
) {
    val totalValue = summary?.totalValue ?: 0.0
    val totalPnL = summary?.totalPnL ?: 0.0
    val totalPnLPercent = summary?.totalPnLPercent ?: 0.0
    val isPositive = totalPnL >= 0
    val changeColor = if (isPositive) ProfitGreenLight else LossRedLight
    val changeIcon = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown

    GradientCard(modifier = modifier) {
        Text(
            text = "Toplam Portföy Değeri",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = Formatters.formatPrice(totalValue),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        changeIcon, null,
                        modifier = Modifier.size(14.dp), tint = changeColor
                    )
                    Text(
                        text = "${Formatters.formatPnL(totalPnL)} (${Formatters.formatPercent(totalPnLPercent)})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = changeColor
                    )
                }
            }
            Text(
                text = "toplam",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

// ===== Market Indices Row =====
@Composable
private fun MarketIndicesRow(indices: List<MarketIndex>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(indices) { index ->
            MarketIndexCard(index)
        }
    }
}

@Composable
private fun MarketIndexCard(index: MarketIndex) {
    val isPositive = index.changePercent >= 0
    val changeColor = if (isPositive) ProfitGreen else LossRed
    val changeBgColor = if (isPositive) ProfitGreenBg else LossRedBg
    val trendIcon = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown

    val iconEmoji = when {
        index.symbol.startsWith("XU") -> "📈"
        index.symbol.contains("USD") -> "💵"
        index.symbol.contains("EUR") -> "💶"
        index.symbol.contains("BTC") -> "₿"
        index.symbol.contains("GC") -> "🥇"
        else -> "📊"
    }

    GlassCardSmall(modifier = Modifier.width(165.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = iconEmoji,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = index.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = Formatters.formatIndexPrice(index.price, index.symbol),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(changeBgColor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(trendIcon, null, modifier = Modifier.size(12.dp), tint = changeColor)
                Text(
                    text = Formatters.formatPercent(index.changePercent),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = changeColor
                )
            }
        }
    }
}

// ===== Market Summary Bar =====
@Composable
private fun MarketSummaryBar(summary: com.investia.app.domain.model.MarketSummary) {
    val trendColor = when (summary.marketTrend.lowercase()) {
        "bullish" -> ProfitGreen
        "bearish" -> LossRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val trendText = when (summary.marketTrend.lowercase()) {
        "bullish" -> "Yükseliş"
        "bearish" -> "Düşüş"
        else -> "Yatay"
    }
    val trendIcon = when (summary.marketTrend.lowercase()) {
        "bullish" -> Icons.AutoMirrored.Filled.TrendingUp
        "bearish" -> Icons.AutoMirrored.Filled.TrendingDown
        else -> Icons.AutoMirrored.Filled.ShowChart
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(trendIcon, null, modifier = Modifier.size(16.dp), tint = trendColor)
            Text(trendText, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = trendColor)
        }
        Text(
            text = "▲${summary.advancing}  ▼${summary.declining}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Ort: ${Formatters.formatPercent(summary.avgChange)}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (summary.avgChange >= 0) ProfitGreen else LossRed
        )
    }
}

// ===== Quick Actions Grid =====
@Composable
private fun QuickActionsGrid(navController: NavController) {
    val actions = listOf(
        Triple("Sinyaller", Icons.Outlined.Insights, Screen.SignalCenter.route),
        Triple("Haberler", Icons.Outlined.Newspaper, Screen.News.route),
        Triple("Hesaplayıcı", Icons.Outlined.Calculate, Screen.Calculator.route),
        Triple("AI Asistan", Icons.Outlined.SmartToy, Screen.AIChat.route),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        actions.forEach { (label, icon, route) ->
            QuickActionCard(
                label = label,
                icon = icon,
                onClick = { navController.navigate(route) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCardSmall(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(InvestiaPrimary.copy(alpha = 0.2f), InvestiaAccent.copy(alpha = 0.1f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = InvestiaPrimaryLight)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
