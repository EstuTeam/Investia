package com.midastrading.app.presentation.screens.signals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavController
import com.midastrading.app.presentation.components.*
import com.midastrading.app.presentation.navigation.Screen
import com.midastrading.app.presentation.theme.*

@Composable
fun SignalCenterScreen(navController: NavController) {
    val bist30 = listOf(
        "THYAO", "GARAN", "AKBNK", "YKBNK", "EREGL", "BIMAS", "ASELS",
        "KCHOL", "SAHOL", "SISE", "TCELL", "TUPRS", "PGSUS", "FROTO", "TOASO"
    )

    var selectedTab by remember { mutableIntStateOf(0) }

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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text("Sinyal Merkezi", style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text("BIST30 hisseleri için sinyaller",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Strategy tabs
        PillTabRow(
            tabs = listOf("Hybrid", "Teknik", "Momentum"),
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Signals list
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bist30) { symbol ->
                SignalRow(
                    symbol = symbol,
                    onClick = {
                        navController.navigate(Screen.StockDetail.createRoute("$symbol.IS"))
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SignalRow(
    symbol: String,
    onClick: () -> Unit
) {
    GlassCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Symbol circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MidasPrimary.copy(alpha = 0.25f), MidasAccent.copy(alpha = 0.1f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.take(2),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MidasPrimaryLight
                    )
                }
                Column {
                    Text(
                        text = symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Analiz için dokunun",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Sinyal →",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MidasPrimary
                )
            }
        }
    }
}
