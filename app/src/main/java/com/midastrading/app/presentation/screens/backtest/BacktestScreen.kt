package com.midastrading.app.presentation.screens.backtest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.midastrading.app.domain.model.BacktestTrade
import com.midastrading.app.presentation.components.*
import com.midastrading.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BacktestScreen(
    navController: NavController,
    viewModel: BacktestViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val daysOptions = listOf(7, 14, 30, 60, 90)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = MaterialTheme.colorScheme.onBackground)
                }
                Text(
                    "Backtest",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Days selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                daysOptions.forEach { days ->
                    val selected = state.selectedDays == days
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.setDays(days) },
                        label = { Text("${days}G") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MidasPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = MidasPrimary,
                            containerColor = Color.Transparent,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Run button
        item {
            Button(
                onClick = { viewModel.runBacktest(state.selectedDays) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = !state.isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = MidasPrimary),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                if (state.isRunning) {
                    MidasLoadingSpinner(
                        modifier = Modifier,
                        color = Color.White,
                        size = 18.dp,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Çalışıyor...")
                } else {
                    Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Backtest Çalıştır", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error
        state.error?.let { error ->
            item {
                GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(error, color = LossRed, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Results
        state.result?.let { result ->
            // Summary cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard("Toplam İşlem", "${result.totalTrades}", Icons.Outlined.Receipt, MidasPrimary, Modifier.weight(1f))
                    MetricCard("Kazanma %", "%${String.format("%.1f", result.winRate)}", Icons.Outlined.EmojiEvents, ProfitGreen, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard("Toplam Getiri", "%${String.format("%.1f", result.totalReturn)}", Icons.Outlined.TrendingUp,
                        if (result.totalReturn >= 0) ProfitGreen else LossRed, Modifier.weight(1f))
                    MetricCard("Max Düşüş", "%${String.format("%.1f", result.maxDrawdown)}", Icons.Outlined.TrendingDown, LossRed, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard("Kâr Faktörü", String.format("%.2f", result.profitFactor), Icons.Outlined.Analytics, MidasAccent, Modifier.weight(1f))
                    MetricCard("Sharpe", String.format("%.2f", result.sharpeRatio), Icons.Outlined.Insights, MidasSecondary, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Trade history header
            if (result.trades.isNotEmpty()) {
                item {
                    Text(
                        "İşlem Geçmişi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                items(result.trades) { trade ->
                    TradeCard(trade)
                }
            }
        }

        // Loading initial
        if (state.isLoading && state.result == null) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    MidasLoadingSpinner()
                }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    GlassCardSmall(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = color)
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TradeCard(trade: BacktestTrade) {
    val isProfit = trade.profitLoss >= 0
    GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background((if (isProfit) ProfitGreen else LossRed).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isProfit) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                    null, tint = if (isProfit) ProfitGreen else LossRed,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(trade.symbol, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    "${trade.entryDate} → ${trade.exitDate}",
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (isProfit) "+" else ""}₺${String.format("%.2f", trade.profitLoss)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isProfit) ProfitGreen else LossRed
                )
                Text(
                    "${if (isProfit) "+" else ""}${String.format("%.1f", trade.profitLossPercent)}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isProfit) ProfitGreen else LossRed
                )
            }
        }
    }
}
