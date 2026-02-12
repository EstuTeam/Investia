package com.investia.app.presentation.screens.ipo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.investia.app.domain.model.IPOItem
import com.investia.app.domain.model.IPOStatus
import com.investia.app.presentation.components.*
import com.investia.app.presentation.theme.*

@Composable
fun IPOScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    viewModel: IPOViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val tabs = listOf("Tümü", "Aktif", "Yaklaşan", "Tamamlanan")

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Halka Arz",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "G\u00fcncel halka arz takibi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { viewModel.loadIPOs() }) {
                    Icon(Icons.Filled.Refresh, "Yenile", tint = InvestiaPrimary)
                }
            }
        }

        // Stats cards
        state.stats?.let { stats ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IPOStatCard("Toplam", "${stats.totalIPOs}", InvestiaPrimary, Modifier.weight(1f))
                    IPOStatCard("Aktif", "${stats.activeIPOs}", ProfitGreen, Modifier.weight(1f))
                    IPOStatCard("Yaklaşan", "${stats.upcomingIPOs}", WarningOrange, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                GlassCardSmall(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Outlined.TrendingUp, null, tint = ProfitGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ort. Getiri: ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "%${String.format("%.1f", stats.avgReturn)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (stats.avgReturn >= 0) ProfitGreen else LossRed
                        )
                    }
                }
            }
        }

        // Tab row
        item {
            Spacer(modifier = Modifier.height(16.dp))
            ScrollableTabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = Color.Transparent,
                contentColor = InvestiaPrimary,
                edgePadding = 16.dp,
                indicator = {},
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    val selected = state.selectedTab == index
                    Tab(
                        selected = selected,
                        onClick = { viewModel.selectTab(index) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selected) InvestiaPrimary.copy(alpha = 0.15f) else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                if (selected) InvestiaPrimary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            title,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            color = if (selected) InvestiaPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        // Loading
        if (state.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    InvestiaLoadingSpinner()
                }
            }
        }

        // Error
        state.error?.let { error ->
            item {
                GlassCard(modifier = Modifier.padding(16.dp)) {
                    Text(error, color = LossRed, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // IPO List
        val filtered = state.allIPOs.let { ipos ->
            when (state.selectedTab) {
                1 -> ipos.filter { it.status == IPOStatus.ACTIVE }
                2 -> ipos.filter { it.status == IPOStatus.UPCOMING }
                3 -> ipos.filter { it.status == IPOStatus.COMPLETED || it.status == IPOStatus.TRADING }
                else -> ipos
            }
        }

        if (!state.isLoading && filtered.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Outlined.Business, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Halka arz bulunamadı", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        items(filtered, key = { it.symbol }) { ipo ->
            IPOCard(ipo)
        }
    }
}

@Composable
private fun IPOCard(ipo: IPOItem) {
    GlassCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor(ipo.status).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (ipo.status) {
                        IPOStatus.ACTIVE -> Icons.Filled.PlayArrow
                        IPOStatus.UPCOMING -> Icons.Filled.Schedule
                        IPOStatus.COMPLETED -> Icons.Filled.CheckCircle
                        IPOStatus.TRADING -> Icons.AutoMirrored.Filled.ShowChart
                    },
                    null, tint = statusColor(ipo.status), modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(ipo.symbol, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(ipo.status)
                }
                Text(ipo.companyName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (ipo.sector.isNotBlank()) {
                    Text(ipo.sector, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (ipo.price > 0) {
                    Text("₺${String.format("%.2f", ipo.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }
                if (ipo.returnPercent != 0.0) {
                    Text(
                        "${if (ipo.returnPercent > 0) "+" else ""}${String.format("%.1f", ipo.returnPercent)}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (ipo.returnPercent >= 0) ProfitGreen else LossRed
                    )
                }
            }
        }

        // Details row
        if (ipo.startDate.isNotBlank() || ipo.demandMultiple > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            GradientDivider()
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (ipo.startDate.isNotBlank()) {
                    DetailChip("Başlangıç", ipo.startDate)
                }
                if (ipo.endDate.isNotBlank()) {
                    DetailChip("Bitiş", ipo.endDate)
                }
                if (ipo.demandMultiple > 0) {
                    DetailChip("Talep", "${String.format("%.1f", ipo.demandMultiple)}x")
                }
                if (ipo.lotSize > 0) {
                    DetailChip("Lot", "${ipo.lotSize}")
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: IPOStatus) {
    val color = statusColor(status)
    val text = when (status) {
        IPOStatus.ACTIVE -> "Aktif"
        IPOStatus.UPCOMING -> "Yaklaşan"
        IPOStatus.COMPLETED -> "Tamamlandı"
        IPOStatus.TRADING -> "İşlemde"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
private fun DetailChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(value, style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun IPOStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassCardSmall(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun statusColor(status: IPOStatus): Color = when (status) {
    IPOStatus.ACTIVE -> ProfitGreen
    IPOStatus.UPCOMING -> WarningOrange
    IPOStatus.COMPLETED -> InvestiaPrimary
    IPOStatus.TRADING -> InvestiaAccent
}
