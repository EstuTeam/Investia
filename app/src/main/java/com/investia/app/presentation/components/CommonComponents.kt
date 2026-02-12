package com.investia.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.investia.app.domain.model.SignalType
import com.investia.app.domain.model.StockPick
import com.investia.app.presentation.theme.*
import com.investia.app.util.Formatters

// ===== Custom Loading Spinner (avoids Material3 CircularProgressIndicator keyframes crash) =====
@Composable
fun InvestiaLoadingSpinner(
    modifier: Modifier = Modifier,
    color: Color = InvestiaPrimary,
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    Canvas(modifier = modifier.size(size)) {
        drawArc(
            color = color,
            startAngle = angle,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

// ===== Glassmorphic Card =====
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun GlassCardSmall(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

// ===== Gradient Card =====
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(GradientPrimaryStart, GradientPrimaryMid, GradientPrimaryEnd),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.1f))
        )
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

// ===== Stock Pick Card =====
@Composable
fun StockPickCard(
    pick: StockPick,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick
    ) {
        // Header: Symbol + Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(InvestiaPrimary.copy(alpha = 0.3f), InvestiaAccent.copy(alpha = 0.15f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pick.symbol.replace(".IS", "").take(2),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = InvestiaPrimaryLight
                    )
                }
                Column {
                    Text(
                        text = pick.symbol.replace(".IS", ""),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (pick.name.isNotBlank()) {
                        Text(
                            text = pick.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            ScoreBadge(score = pick.score)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Price + Signal row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Formatters.formatPrice(pick.price),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val changeColor = if (pick.changePercent >= 0) ProfitGreen else LossRed
                Text(
                    text = Formatters.formatPercent(pick.changePercent),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = changeColor
                )
            }
            SignalChip(signal = pick.signal)
        }

        Spacer(modifier = Modifier.height(14.dp))
        GradientDivider()
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TradeLevel("Stop Loss", Formatters.formatPriceShort(pick.stopLoss), LossRed)
            TradeLevel("TP1", Formatters.formatPriceShort(pick.takeProfit1), ProfitGreen)
            TradeLevel("Risk", Formatters.formatPercent(pick.riskPercent), WarningOrange)
            TradeLevel(
                "RSI", "${pick.rsi.toInt()}",
                when {
                    pick.rsi > 70 -> LossRed
                    pick.rsi < 30 -> ProfitGreen
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        if (pick.reasons.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                pick.reasons.take(3).forEach { reason ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(InvestiaPrimary.copy(alpha = 0.12f))
                    ) {
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.labelSmall,
                            color = InvestiaPrimaryLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ===== Score Badge =====
@Composable
fun ScoreBadge(score: Int, modifier: Modifier = Modifier) {
    val color = when {
        score >= 80 -> ScoreExcellent
        score >= 60 -> ScoreGood
        score >= 40 -> ScoreAverage
        else -> ScorePoor
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// ===== Signal Chip =====
@Composable
fun SignalChip(signal: SignalType, modifier: Modifier = Modifier) {
    val (text, color) = when (signal) {
        SignalType.STRONG_BUY -> "Güçlü AL" to SignalStrongBuy
        SignalType.BUY -> "AL" to SignalBuy
        SignalType.HOLD -> "TUT" to SignalHold
        SignalType.SELL -> "SAT" to SignalSell
        SignalType.STRONG_SELL -> "Güçlü SAT" to SignalStrongSell
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

// ===== Trade Level =====
@Composable
fun TradeLevel(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// ===== PnL Text =====
@Composable
fun PnLText(
    value: Double,
    percent: Double? = null,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (value >= 0) ProfitGreen else LossRed,
        animationSpec = tween(300),
        label = "pnl_color"
    )
    val text = buildString {
        append(Formatters.formatPnL(value))
        if (percent != null) append(" (${Formatters.formatPercent(percent)})")
    }
    Text(text = text, style = style, fontWeight = FontWeight.SemiBold, color = color, modifier = modifier)
}

// ===== Section Header =====
@Composable
fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (action != null) {
            TextButton(onClick = onAction) {
                Text(text = action, style = MaterialTheme.typography.labelLarge, color = InvestiaPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(14.dp), tint = InvestiaPrimary)
            }
        }
    }
}

// ===== Loading Screen =====
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "loading")
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )
            val trackColor = MaterialTheme.colorScheme.surfaceVariant
            Canvas(modifier = Modifier.size(48.dp)) {
                drawArc(
                    color = trackColor, startAngle = 0f, sweepAngle = 360f,
                    useCenter = false, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = InvestiaPrimary, startAngle = angle, sweepAngle = 270f,
                    useCenter = false, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text("Yükleniyor...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ===== Error Screen =====
@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(LossRedBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.ErrorOutline, null, modifier = Modifier.size(32.dp), tint = LossRed)
            }
            Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = InvestiaPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tekrar Dene")
            }
        }
    }
}

// ===== Shimmer Loading =====
@Composable
fun ShimmerBox(modifier: Modifier = Modifier, cornerRadius: Dp = 12.dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f, targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
    )
}

// ===== Gradient Divider =====
@Composable
fun GradientDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, DarkBorderLight, Color.Transparent)
                )
            )
    )
}

// ===== Pill Tab Row =====
@Composable
fun PillTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) InvestiaPrimary else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ===== Strength Bar =====
@Composable
fun StrengthBar(
    value: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = InvestiaPrimary,
    height: Dp = 6.dp
) {
    Box(
        modifier = modifier.fillMaxWidth().height(height)
            .clip(RoundedCornerShape(height / 2)).background(trackColor)
    ) {
        Box(
            modifier = Modifier.fillMaxHeight()
                .fillMaxWidth(fraction = value.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(height / 2))
                .background(Brush.horizontalGradient(listOf(progressColor, progressColor.copy(alpha = 0.6f))))
        )
    }
}
