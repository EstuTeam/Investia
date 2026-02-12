package com.investia.app.presentation.screens.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.investia.app.domain.model.AlertItem
import com.investia.app.domain.model.NotificationItem
import com.investia.app.presentation.components.*
import com.investia.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    navController: NavController,
    viewModel: AlertViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val tabs = listOf("Alarmlar", "Bildirimler")

    // Create Alert Dialog
    if (state.showCreateDialog) {
        CreateAlertDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onCreate = { symbol, type, price -> viewModel.createAlert(symbol, type, price) }
        )
    }

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
                    "Alarmlar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                if (state.selectedTab == 0) {
                    IconButton(onClick = { viewModel.showCreateDialog() }) {
                        Icon(Icons.Filled.Add, "Alarm Ekle", tint = InvestiaPrimary)
                    }
                } else {
                    IconButton(onClick = { viewModel.markAllRead() }) {
                        Icon(Icons.Filled.DoneAll, "Tümünü Oku", tint = InvestiaPrimary)
                    }
                }
            }
        }

        // Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val selected = state.selectedTab == index
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.selectTab(index) },
                        label = { Text(title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                        leadingIcon = {
                            Icon(
                                if (index == 0) Icons.Outlined.NotificationsActive else Icons.Outlined.History,
                                null, modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = InvestiaPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = InvestiaPrimary,
                            selectedLeadingIconColor = InvestiaPrimary,
                            containerColor = Color.Transparent,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Loading
        if (state.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    InvestiaLoadingSpinner()
                }
            }
        }

        // Alerts Tab
        if (state.selectedTab == 0 && !state.isLoading) {
            if (state.alerts.isEmpty()) {
                item { EmptyState("Henüz alarm eklenmedi", "Fiyat alarmı oluşturmak için + butonuna tıklayın", Icons.Outlined.NotificationsOff) }
            }
            items(state.alerts, key = { it.id }) { alert ->
                AlertCard(
                    alert = alert,
                    onToggle = { viewModel.toggleAlert(alert.id) },
                    onDelete = { viewModel.deleteAlert(alert.id) }
                )
            }
        }

        // Notifications Tab
        if (state.selectedTab == 1 && !state.isLoading) {
            if (state.notifications.isEmpty()) {
                item { EmptyState("Bildirim yok", "Tetiklenen alarmlar burada görünecek", Icons.Outlined.Notifications) }
            }
            items(state.notifications, key = { it.id }) { notification ->
                NotificationCard(
                    notification = notification,
                    onRead = { viewModel.markNotificationRead(notification.id) }
                )
            }
        }
    }
}

@Composable
private fun AlertCard(alert: AlertItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        (if (alert.active) InvestiaPrimary else MaterialTheme.colorScheme.outline).copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (alert.type.contains("above")) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    null, tint = if (alert.active) InvestiaPrimary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(alert.symbol, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    "${alertTypeLabel(alert.type)} ₺${String.format("%.2f", alert.targetPrice)}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = alert.active,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = InvestiaPrimary,
                    checkedTrackColor = InvestiaPrimary.copy(alpha = 0.3f),
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, "Sil", tint = LossRed.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationItem, onRead: () -> Unit) {
    GlassCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = { if (!notification.isRead) onRead() }
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        priorityColor(notification.priority).copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Notifications, null,
                    tint = priorityColor(notification.priority),
                    modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(notification.title, style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp))
                                .background(InvestiaPrimary)
                        )
                    }
                }
                Text(notification.message, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                if (notification.createdAt.isNotBlank()) {
                    Text(notification.createdAt, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAlertDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, Double) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var targetPrice by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("price_above") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        title = { Text("Yeni Alarm", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = symbol, onValueChange = { symbol = it.uppercase() },
                    label = { Text("Hisse Kodu") },
                    placeholder = { Text("Örn: THYAO") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InvestiaPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = InvestiaPrimary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = InvestiaPrimary
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("price_above" to "Üzerine", "price_below" to "Altına").forEach { (type, label) ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
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
                OutlinedTextField(
                    value = targetPrice, onValueChange = { targetPrice = it },
                    label = { Text("Hedef Fiyat (₺)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InvestiaPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = InvestiaPrimary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = InvestiaPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { targetPrice.toDoubleOrNull()?.let { onCreate(symbol, selectedType, it) } },
                colors = ButtonDefaults.buttonColors(containerColor = InvestiaPrimary),
                enabled = symbol.isNotBlank() && targetPrice.toDoubleOrNull() != null
            ) { Text("Oluştur") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    )
}

private fun alertTypeLabel(type: String): String = when (type) {
    "price_above" -> "Fiyat üzerine çıktığında:"
    "price_below" -> "Fiyat altına düştüğünde:"
    "score_above" -> "Skor üzerine çıktığında:"
    else -> type
}

private fun priorityColor(priority: String): Color = when (priority) {
    "high" -> LossRed
    "medium" -> WarningOrange
    else -> InvestiaPrimary
}
