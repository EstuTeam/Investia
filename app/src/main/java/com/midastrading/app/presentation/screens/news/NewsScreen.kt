package com.midastrading.app.presentation.screens.news

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.midastrading.app.presentation.components.*
import com.midastrading.app.presentation.theme.*

@Composable
fun NewsScreen(navController: NavController) {
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
            Text("Haberler", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }

        // Tab row
        var selectedTab by remember { mutableIntStateOf(0) }
        PillTabRow(
            tabs = listOf("Ekonomi", "Gündem", "Borsa"),
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // News list
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sampleNewsTitles.size) { index ->
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Category badge
                            val categories = listOf("Ekonomi", "Gündem", "Borsa", "Döviz", "Altın")
                            val categoryColors = listOf(MidasPrimary, MidasAccent, ProfitGreen, WarningOrange, MidasSecondary)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(categoryColors[index % 5].copy(alpha = 0.12f))
                            ) {
                                Text(
                                    text = categories[index % 5],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = categoryColors[index % 5],
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = sampleNewsTitles[index % sampleNewsTitles.size],
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = sampleNewsDescriptions[index % sampleNewsDescriptions.size],
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${(index + 1) * 2} saat önce",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Box(
                                    modifier = Modifier
                                        .size(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    text = listOf("Bloomberg", "Reuters", "AA", "Dünya", "Para")[index % 5],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.OpenInNew, null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

private val sampleNewsTitles = listOf(
    "BIST 100 endeksi güne yükselişle başladı",
    "Merkez Bankası faiz kararını açıkladı",
    "Dolar/TL'de son durum: Piyasalar ne bekliyor?",
    "Altın fiyatları rekor kırmaya devam ediyor",
    "Teknoloji hisseleri rallisini sürdürüyor",
    "Enflasyon verileri beklentilerin altında kaldı",
    "Petrol fiyatları OPEC toplantısı öncesi yükseldi",
    "Avrupa borsaları karışık seyirle kapandı"
)

private val sampleNewsDescriptions = listOf(
    "Borsa İstanbul güne alış ağırlıklı başlarken, bankacılık sektörü endekse en büyük katkıyı sağladı.",
    "TCMB, politika faizini sabit tutma kararı aldı. Piyasa beklentileri bu yöndeydi.",
    "Dolar/TL paritesi dar bantta hareket ederken, yatırımcılar Fed kararlarını takip ediyor.",
    "Ons altın 2.400 dolar seviyesini test etti. Gram altın da rekor kırdı.",
    "ABD teknoloji devlerinin güçlü bilançoları sektörel ralliyi destekliyor.",
    "TÜİK verilerine göre aylık enflasyon %1.64 olarak gerçekleşti.",
    "Brent petrol varili 85 doların üzerine çıktı. Arz kısıntıları fiyatları destekliyor.",
    "DAX ve FTSE 100 endeksleri günü farklı yönlerde tamamladı."
)
