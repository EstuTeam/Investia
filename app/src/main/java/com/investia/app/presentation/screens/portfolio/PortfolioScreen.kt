package com.investia.app.presentation.screens.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.investia.app.presentation.components.*
import com.investia.app.presentation.navigation.Screen
import com.investia.app.presentation.theme.*
import com.investia.app.util.Formatters

@Composable
fun PortfolioScreen(
    navController: NavController,
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Portföy",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ProfitGreen.copy(alpha = 0.12f))
                        .border(1.dp, ProfitGreen.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { viewModel.showCreateDialog() }) {
                        Icon(Icons.Filled.Add, "Ekle", tint = ProfitGreen, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        when {
            state.isLoading -> {
                item { LoadingScreen() }
            }
            state.error != null && state.portfolios.isEmpty() -> {
                item {
                    ErrorScreen(
                        message = state.error!!,
                        onRetry = { viewModel.loadPortfolios() }
                    )
                }
            }
            state.portfolios.isEmpty() -> {
                // Empty - no portfolios
                item {
                    GradientCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colors = listOf(GradientPrimaryStart, GradientPrimaryMid, GradientPrimaryEnd)
                    ) {
                        Text("Toplam Değer", color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("₺0,00", color = Color.White,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(InvestiaPrimary.copy(alpha = 0.15f), InvestiaAccent.copy(alpha = 0.08f))
                                    )
                                )
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.AccountBalance, null,
                                modifier = Modifier.size(36.dp), tint = InvestiaPrimaryLight
                            )
                        }
                        Text(
                            "Portföyünüz henüz boş",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Günün fırsatlarından hisse seçerek\nportföyünüzü oluşturmaya başlayın",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { navController.navigate("daily_picks") },
                            colors = ButtonDefaults.buttonColors(containerColor = InvestiaPrimary),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fırsatları Keşfet", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
            else -> {
                // Portfolio tabs if multiple
                if (state.portfolios.size > 1) {
                    item {
                        PillTabRow(
                            tabs = state.portfolios.map { it.name },
                            selectedIndex = state.portfolios.indexOf(state.selectedPortfolio).coerceAtLeast(0),
                            onTabSelected = { viewModel.selectPortfolio(state.portfolios[it]) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                val portfolio = state.selectedPortfolio ?: state.portfolios.first()

                // Portfolio Value Card
                item {
                    GradientCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        colors = listOf(GradientPrimaryStart, GradientPrimaryMid, GradientPrimaryEnd)
                    ) {
                        Text("Toplam Değer", color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = Formatters.formatPrice(portfolio.totalValue),
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("Maliyet", color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(Formatters.formatPrice(portfolio.totalCost),
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Column {
                                Text("K/Z", color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    val pnl = portfolio.totalValue - portfolio.totalCost
                                    val pnlColor = if (pnl >= 0) ProfitGreenLight else LossRedLight
                                    Text(Formatters.formatPnL(pnl), color = pnlColor,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                // Stats Row
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMiniCard("Hisse", "${portfolio.holdings.size}",
                            Icons.Outlined.BarChart, Modifier.weight(1f))
                        StatMiniCard("Kazançlı", "${portfolio.holdings.count { it.profitLoss >= 0 }}",
                            Icons.AutoMirrored.Outlined.TrendingUp, Modifier.weight(1f))
                        StatMiniCard("Kayıplı", "${portfolio.holdings.count { it.profitLoss < 0 }}",
                            Icons.AutoMirrored.Outlined.TrendingDown, Modifier.weight(1f))
                    }
                }

                // Holdings section
                if (portfolio.holdings.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        SectionHeader(title = "Pozisyonlar", modifier = Modifier.padding(horizontal = 16.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(portfolio.holdings) { holding ->
                        GlassCard(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            onClick = { navController.navigate(Screen.StockDetail.createRoute(holding.symbol)) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(InvestiaPrimary.copy(alpha = 0.25f), InvestiaAccent.copy(alpha = 0.1f))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = holding.symbol.replace(".IS", "").take(2),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = InvestiaPrimaryLight
                                        )
                                    }
                                    Column {
                                        Text(holding.symbol.replace(".IS", ""),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onBackground)
                                        Text("${holding.quantity.toInt()} adet • ${Formatters.formatPrice(holding.avgCost)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(Formatters.formatPrice(holding.totalValue),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground)
                                    val pnlColor = if (holding.profitLoss >= 0) ProfitGreen else LossRed
                                    Text("${Formatters.formatPnL(holding.profitLoss)} (${Formatters.formatPercent(holding.profitLossPercent)})",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = pnlColor)
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Bu portföyde henüz pozisyon yok",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { navController.navigate("daily_picks") },
                                colors = ButtonDefaults.buttonColors(containerColor = InvestiaPrimary),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Fırsatları Keşfet") }
                        }
                    }
                }
            }
        }
    }

    // Create Portfolio Dialog
    if (state.showCreateDialog) {
        CreatePortfolioDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onCreate = { name, desc -> viewModel.createPortfolio(name, desc) }
        )
    }
}

@Composable
private fun CreatePortfolioDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Portföy", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Portföy Adı") },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Açıklama (opsiyonel)") },
                    singleLine = true, shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, description.ifBlank { null }) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = InvestiaPrimary),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Oluştur") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun StatMiniCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    GlassCardSmall(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = InvestiaPrimary)
            Column {
                Text(value, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(label, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
