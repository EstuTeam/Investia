package com.midastrading.app.presentation.screens.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.midastrading.app.presentation.components.*
import com.midastrading.app.presentation.theme.*

@Composable
fun PortfolioScreen(navController: NavController) {
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
                    Icon(Icons.Filled.Add, "Ekle", tint = ProfitGreen, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Portfolio Value Card
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
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Daily PnL
                    Column {
                        Text("Günlük K/Z", color = Color.White.copy(0.6f),
                            style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+₺0,00", color = ProfitGreenLight,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                    // Total PnL
                    Column {
                        Text("Toplam K/Z", color = Color.White.copy(0.6f),
                            style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("+₺0,00", color = ProfitGreenLight,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMiniCard("Hisse", "0", Icons.Outlined.BarChart, Modifier.weight(1f))
                StatMiniCard("Kazançlı", "0", Icons.AutoMirrored.Outlined.TrendingUp, Modifier.weight(1f))
                StatMiniCard("Kayıplı", "0", Icons.AutoMirrored.Outlined.TrendingDown, Modifier.weight(1f))
            }
        }

        // Empty state
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
                                listOf(MidasPrimary.copy(alpha = 0.15f), MidasAccent.copy(alpha = 0.08f))
                            )
                        )
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.AccountBalance, null,
                        modifier = Modifier.size(36.dp), tint = MidasPrimaryLight
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
                    colors = ButtonDefaults.buttonColors(containerColor = MidasPrimary),
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
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = MidasPrimary)
            Column {
                Text(value, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
