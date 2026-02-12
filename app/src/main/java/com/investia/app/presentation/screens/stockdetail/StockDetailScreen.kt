package com.investia.app.presentation.screens.stockdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.investia.app.presentation.components.*
import com.investia.app.presentation.theme.*
import com.investia.app.util.Formatters

@Composable
fun StockDetailScreen(
    symbol: String,
    navController: NavController,
    viewModel: StockDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadStock(symbol)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = symbol.replace(".IS", ""),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { }) {
                Icon(Icons.Filled.FavoriteBorder, "Watchlist", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { }) {
                Icon(Icons.Filled.NotificationsNone, "Alarm", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        when {
            state.isLoading -> LoadingScreen()
            state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.loadStock(symbol) })
            state.quote != null -> {
                val quote = state.quote!!
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Price header
                    item {
                        GlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = quote.name.ifBlank { symbol.replace(".IS", "") },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = Formatters.formatPrice(quote.price),
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    PnLText(
                                        value = quote.change,
                                        percent = quote.changePercent,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                if (state.signal != null) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        SignalChip(signal = state.signal!!.signal)
                                        if (state.signal!!.score > 0) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            ScoreBadge(score = state.signal!!.score)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Chart placeholder
                    item {
                        GlassCard {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(InvestiaPrimary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ShowChart, null,
                                            modifier = Modifier.size(24.dp), tint = InvestiaPrimary)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Grafik yakında eklenecek",
                                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Time period selector
                            Spacer(modifier = Modifier.height(12.dp))
                            var selectedPeriod by remember { mutableIntStateOf(2) }
                            PillTabRow(
                                tabs = listOf("1G", "1H", "1A", "3A", "1Y"),
                                selectedIndex = selectedPeriod,
                                onTabSelected = { selectedPeriod = it }
                            )
                        }
                    }

                    // Key stats
                    item {
                        SectionHeader(title = "Temel Bilgiler")
                    }
                    item {
                        GlassCard {
                            StatRow("Önceki Kapanış", Formatters.formatPriceShort(quote.previousClose) + " ₺")
                            GradientDivider(modifier = Modifier.padding(vertical = 8.dp))
                            StatRow("Gün İçi Yüksek", Formatters.formatPriceShort(quote.dayHigh) + " ₺",
                                valueColor = ProfitGreen)
                            GradientDivider(modifier = Modifier.padding(vertical = 8.dp))
                            StatRow("Gün İçi Düşük", Formatters.formatPriceShort(quote.dayLow) + " ₺",
                                valueColor = LossRed)
                            GradientDivider(modifier = Modifier.padding(vertical = 8.dp))
                            StatRow("Hacim", Formatters.formatVolume(quote.volume))
                            if (quote.marketCap > 0) {
                                GradientDivider(modifier = Modifier.padding(vertical = 8.dp))
                                StatRow("Piyasa Değeri", Formatters.formatLargeNumber(quote.marketCap.toDouble()))
                            }
                        }
                    }

                    // Signal section
                    if (state.signal != null) {
                        item {
                            SectionHeader(title = "Teknik Sinyal")
                        }
                        item {
                            GlassCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Sinyal Tipi", style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        SignalChip(signal = state.signal!!.signal)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Güç Skoru", style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        ScoreBadge(score = state.signal!!.score)
                                    }
                                }
                                if (state.signal!!.rsi > 0) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    GradientDivider()
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("RSI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${state.signal!!.rsi.toInt()}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    state.signal!!.rsi > 70 -> LossRed
                                                    state.signal!!.rsi < 30 -> ProfitGreen
                                                    else -> MaterialTheme.colorScheme.onBackground
                                                }
                                            )
                                        }
                                        if (state.signal!!.macdSignal.isNotBlank()) {
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("MACD", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(state.signal!!.macdSignal,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold, color = InvestiaPrimary)
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("RSI Seviyesi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    StrengthBar(
                                        value = (state.signal!!.rsi / 100f).toFloat(),
                                        progressColor = when {
                                            state.signal!!.rsi > 70 -> LossRed
                                            state.signal!!.rsi < 30 -> ProfitGreen
                                            else -> InvestiaPrimary
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Bottom spacing
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold, color = valueColor ?: MaterialTheme.colorScheme.onBackground)
    }
}
