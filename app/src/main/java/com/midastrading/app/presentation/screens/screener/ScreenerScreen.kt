package com.midastrading.app.presentation.screens.screener

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.midastrading.app.presentation.components.*
import com.midastrading.app.presentation.navigation.Screen
import com.midastrading.app.presentation.theme.*

@Composable
fun ScreenerScreen(
    navController: NavController,
    viewModel: ScreenerViewModel = hiltViewModel()
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
                    text = "Hisse Tarama",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${state.results.size} hisse analiz edildi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.Sort, "Sırala", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MidasPrimary.copy(alpha = 0.12f))
                        .border(1.dp, MidasPrimary.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.FilterList, "Filtrele", tint = MidasPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Filter pills
        var selectedFilter by remember { mutableIntStateOf(0) }
        PillTabRow(
            tabs = listOf("Tümü", "Yükselenler", "Düşenler", "Yüksek Hacim"),
            selectedIndex = selectedFilter,
            onTabSelected = { selectedFilter = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            state.isLoading -> LoadingScreen()
            state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.loadScreener() })
            else -> {
                val filteredResults = when (selectedFilter) {
                    1 -> state.results.filter { it.changePercent > 0 }
                    2 -> state.results.filter { it.changePercent < 0 }
                    3 -> state.results.filter { it.score >= 70 }
                    else -> state.results
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredResults) { pick ->
                        StockPickCard(
                            pick = pick,
                            onClick = {
                                navController.navigate(Screen.StockDetail.createRoute(pick.symbol))
                            }
                        )
                    }

                    // Bottom padding
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
