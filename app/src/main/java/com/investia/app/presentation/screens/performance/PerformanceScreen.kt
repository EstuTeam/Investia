package com.investia.app.presentation.screens.performance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.investia.app.presentation.components.*
import com.investia.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen(
    navController: NavController,
    viewModel: PerformanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val periods = listOf("7" to "1H", "14" to "2H", "30" to "1A", "60" to "2A", "90" to "3A")

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
                    "Performans",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Period selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEach { (value, label) ->
                    val selected = state.selectedPeriod == value
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.setPeriod(value) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = InvestiaPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = InvestiaPrimary,
                            containerColor = Color.Transparent,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Loading
        if (state.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    InvestiaLoadingSpinner()
                }
            }
        }

        state.error?.let { error ->
            item {
                GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(error, color = LossRed, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        state.backtestResult?.let { result ->
            // Summary card
            item {
                GradientCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = listOf(GradientPrimaryStart, GradientPrimaryMid, GradientPrimaryEnd)
                ) {
                    Text("Toplam Getiri", color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "%${String.format("%.2f", result.totalReturn)}",
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Column {
                            Text("İşlem", color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                            Text("${result.totalTrades}", color = Color.White,
                                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Kazanma", color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                            Text("%${String.format("%.0f", result.winRate)}", color = Color.White,
                                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Sharpe", color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                            Text(String.format("%.2f", result.sharpeRatio), color = Color.White,
                                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Detail metrics
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCardSmall(modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.AutoMirrored.Outlined.TrendingDown, null, tint = LossRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("%${String.format("%.1f", result.maxDrawdown)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, color = LossRed)
                            Text("Max Düşüş", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    GlassCardSmall(modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Outlined.Analytics, null, tint = InvestiaAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(String.format("%.2f", result.profitFactor),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, color = InvestiaAccent)
                            Text("Kâr Faktörü", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Recent trades
            if (result.trades.isNotEmpty()) {
                item {
                    Text(
                        "Son İşlemler",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                items(result.trades.take(20)) { trade ->
                    val isProfit = trade.profitLoss >= 0
                    GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background((if (isProfit) ProfitGreen else LossRed).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (isProfit) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                    null, tint = if (isProfit) ProfitGreen else LossRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(trade.symbol, style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                                Text(trade.signal, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Text(
                                "${if (isProfit) "+" else ""}${String.format("%.1f", trade.profitLossPercent)}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isProfit) ProfitGreen else LossRed
                            )
                        }
                    }
                }
            }
        }
    }
}
