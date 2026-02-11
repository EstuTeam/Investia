package com.midastrading.app.presentation.screens.dailypicks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.midastrading.app.presentation.components.*
import com.midastrading.app.presentation.navigation.Screen
import com.midastrading.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPicksScreen(
    navController: NavController,
    viewModel: DailyPicksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Günün Fırsatları",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Hybrid strateji ile seçilmiş en iyi fırsatlar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { viewModel.refresh() },
                enabled = !state.isRefreshing
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MidasPrimary.copy(alpha = 0.12f))
                        .border(1.dp, MidasPrimary.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Refresh, "Yenile", tint = MidasPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }

        when {
            state.isLoading -> LoadingScreen()
            state.error != null -> ErrorScreen(
                message = state.error!!,
                onRetry = { viewModel.loadPicks() }
            )
            state.picks.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.SearchOff, null,
                                modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "Bugün henüz fırsat bulunamadı",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Piyasa açıldığında yeni fırsatlar gelecek",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Strategy info card
                    item {
                        StrategyInfoCard()
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Picks list
                    itemsIndexed(state.picks) { index, pick ->
                        Column {
                            if (index == 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Text("🏆", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "EN İYİ FIRSAT",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ProfitGreen
                                    )
                                }
                            }
                            StockPickCard(
                                pick = pick,
                                onClick = {
                                    navController.navigate(
                                        Screen.StockDetail.createRoute(pick.symbol)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StrategyInfoCard() {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MidasPrimary.copy(alpha = 0.1f),
                        MidasAccent.copy(alpha = 0.05f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape
            )
            .border(1.dp, MidasPrimary.copy(alpha = 0.2f), shape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MidasPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.AutoAwesome, null,
                    tint = MidasPrimary, modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hybrid Strateji V3",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MidasPrimaryLight
                )
                Text(
                    text = "EMA + RSI + ATR + Hacim + Momentum analizi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
