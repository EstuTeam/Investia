package com.midastrading.app.presentation.screens.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.midastrading.app.presentation.components.*
import com.midastrading.app.presentation.theme.*
import com.midastrading.app.util.Formatters

@Composable
fun CalculatorScreen(navController: NavController) {
    var capital by remember { mutableStateOf("100000") }
    var riskPercent by remember { mutableStateOf("1") }
    var entryPrice by remember { mutableStateOf("") }
    var stopLossPrice by remember { mutableStateOf("") }

    val capitalValue = capital.toDoubleOrNull() ?: 0.0
    val riskPct = riskPercent.toDoubleOrNull() ?: 0.0
    val entry = entryPrice.toDoubleOrNull() ?: 0.0
    val stop = stopLossPrice.toDoubleOrNull() ?: 0.0
    val riskPerShare = if (entry > 0 && stop > 0) kotlin.math.abs(entry - stop) else 0.0
    val riskAmount = capitalValue * (riskPct / 100.0)
    val positionSize = if (riskPerShare > 0) (riskAmount / riskPerShare).toInt() else 0
    val totalCost = positionSize * entry
    val tp1 = if (riskPerShare > 0) entry + (riskPerShare * 1.5) else 0.0
    val tp2 = if (riskPerShare > 0) entry + (riskPerShare * 2.5) else 0.0

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MidasPrimary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        cursorColor = MidasPrimary
    )

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
            Spacer(modifier = Modifier.width(4.dp))
            Text("Pozisyon Hesaplayıcı", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Input fields in glass card
            GlassCard {
                OutlinedTextField(
                    value = capital, onValueChange = { capital = it },
                    label = { Text("Sermaye (₺)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = riskPercent, onValueChange = { riskPercent = it },
                    label = { Text("Risk Oranı (%)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = entryPrice, onValueChange = { entryPrice = it },
                    label = { Text("Giriş Fiyatı (₺)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = stopLossPrice, onValueChange = { stopLossPrice = it },
                    label = { Text("Stop Loss Fiyatı (₺)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
            }

            // Results
            GlassCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MidasPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Calculate, null, modifier = Modifier.size(20.dp), tint = MidasPrimary)
                    }
                    Text("Hesaplama Sonuçları", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }

                Spacer(modifier = Modifier.height(14.dp))
                GradientDivider()
                Spacer(modifier = Modifier.height(14.dp))

                ResultRow("Risk Tutarı", Formatters.formatPrice(riskAmount))
                Spacer(modifier = Modifier.height(8.dp))
                ResultRow("Hisse Başı Risk", Formatters.formatPriceShort(riskPerShare) + " ₺")
                Spacer(modifier = Modifier.height(8.dp))
                ResultRow("Pozisyon Büyüklüğü", "$positionSize lot", MidasPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                ResultRow("Toplam Maliyet", Formatters.formatPrice(totalCost))

                Spacer(modifier = Modifier.height(14.dp))
                GradientDivider()
                Spacer(modifier = Modifier.height(14.dp))

                ResultRow("TP1 (R:R 1:1.5)", Formatters.formatPriceShort(tp1) + " ₺", ProfitGreen)
                Spacer(modifier = Modifier.height(8.dp))
                ResultRow("TP2 (R:R 1:2.5)", Formatters.formatPriceShort(tp2) + " ₺", ProfitGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, valueColor: Color? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold, color = valueColor ?: MaterialTheme.colorScheme.onBackground)
    }
}
