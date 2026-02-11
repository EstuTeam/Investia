package com.midastrading.app.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
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
import com.midastrading.app.presentation.components.*
import com.midastrading.app.presentation.navigation.Screen
import com.midastrading.app.presentation.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daha Fazla",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Settings, "Ayarlar", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Profile card
        item {
            GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(MidasPrimary, MidasAccent),
                                    start = Offset(0f, 0f),
                                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, null, modifier = Modifier.size(28.dp), tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Trader", style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("Giriş yaparak tüm özellikleri kullanın",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MidasPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Login, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Giriş Yap", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        // Menu Section: ARAÇLAR
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "ARAÇLAR",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        item {
            GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                MenuRow(Icons.Outlined.Insights, "Sinyal Merkezi", "BIST30 alım/satım sinyalleri", MidasPrimary) {
                    navController.navigate(Screen.SignalCenter.route)
                }
                GradientDivider(modifier = Modifier.padding(vertical = 4.dp))
                MenuRow(Icons.Outlined.Calculate, "Hesaplayıcı", "Pozisyon boyutu hesapla", MidasAccent) {
                    navController.navigate(Screen.Calculator.route)
                }
                GradientDivider(modifier = Modifier.padding(vertical = 4.dp))
                MenuRow(Icons.Outlined.SmartToy, "AI Asistan", "Yatırım danışmanı", MidasSecondary) {
                    navController.navigate(Screen.AIChat.route)
                }
                GradientDivider(modifier = Modifier.padding(vertical = 4.dp))
                MenuRow(Icons.Outlined.Science, "Backtest", "Strateji geri test", ProfitGreen) {
                    navController.navigate(Screen.Backtest.route)
                }
                GradientDivider(modifier = Modifier.padding(vertical = 4.dp))
                MenuRow(Icons.Outlined.BarChart, "Performans", "İşlem performans analizi", MidasAccent) {
                    navController.navigate(Screen.Performance.route)
                }
            }
        }

        // Menu Section: PİYASALAR
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PİYASALAR",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        item {
            GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                MenuRow(Icons.Outlined.Business, "Halka Arz (IPO)", "Yeni halka arz takibi", WarningOrange) {
                    navController.navigate(Screen.IPO.route)
                }
                GradientDivider(modifier = Modifier.padding(vertical = 4.dp))
                MenuRow(Icons.Outlined.NotificationsActive, "Alarmlar", "Fiyat alarmları ve bildirimler", LossRed) {
                    navController.navigate(Screen.Alerts.route)
                }
                GradientDivider(modifier = Modifier.padding(vertical = 4.dp))
                MenuRow(Icons.Outlined.Forum, "Sohbet Odaları", "Yatırımcı sohbetleri", MidasPrimary) {
                    navController.navigate(Screen.ChatRooms.route)
                }
            }
        }

        // Menu Section: HABERLER
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "HABERLER",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        item {
            GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                MenuRow(Icons.Outlined.Newspaper, "Ekonomi Haberleri", "Piyasa ve ekonomi haberleri", ProfitGreen) {
                    navController.navigate(Screen.News.route)
                }
            }
        }

        // Menu Section: AYARLAR
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AYARLAR",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        item {
            GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                MenuRow(Icons.Outlined.Notifications, "Bildirimler", "Push bildirim ayarları", WarningOrange) {
                    navController.navigate(Screen.Alerts.route)
                }
                GradientDivider(modifier = Modifier.padding(vertical = 4.dp))
                ThemeToggleRow()
                GradientDivider(modifier = Modifier.padding(vertical = 4.dp))
                MenuRow(Icons.Outlined.Info, "Hakkında", "Investia v1.0.0", MaterialTheme.colorScheme.onSurfaceVariant) {}
            }
        }
    }
}

@Composable
private fun ThemeToggleRow() {
    val viewModel: ProfileViewModel = hiltViewModel()
    val isDark by viewModel.isDarkTheme.collectAsState(initial = true)

    Surface(
        onClick = { viewModel.toggleTheme() },
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MidasPrimaryLight.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                    null, modifier = Modifier.size(20.dp), tint = MidasPrimaryLight
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Tema", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                Text(if (isDark) "Karanlık mod aktif" else "Aydınlık mod aktif",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = isDark,
                onCheckedChange = { viewModel.toggleTheme() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MidasPrimary,
                    checkedTrackColor = MidasPrimary.copy(alpha = 0.3f),
                    uncheckedThumbColor = WarningOrange,
                    uncheckedTrackColor = WarningOrange.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = iconColor)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.outline)
        }
    }
}
