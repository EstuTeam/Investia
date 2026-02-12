package com.investia.app.presentation.theme

import androidx.compose.ui.graphics.Color

// ===== Core Background Colors (from web: index.css) =====
val DarkBg = Color(0xFF060912)
val DarkSurface = Color(0xFF0F172A)
val DarkSurfaceVariant = Color(0xFF1E293B)
val DarkCard = Color(0xFF0F172A)
val DarkCardHover = Color(0xFF1E293B)
val DarkBorder = Color(0x14FFFFFF) // rgba(255,255,255,0.08)
val DarkBorderLight = Color(0x1AFFFFFF) // rgba(255,255,255,0.1)

// ===== Primary Brand Colors (from web: tailwind.config.js) =====
val InvestiaPrimary = Color(0xFF6366F1)       // indigo-500
val InvestiaPrimaryLight = Color(0xFF818CF8)  // indigo-400
val InvestiaPrimaryDark = Color(0xFF4F46E5)   // indigo-600
val InvestiaSecondary = Color(0xFF8B5CF6)     // violet-500
val InvestiaAccent = Color(0xFF06B6D4)        // cyan-500
val InvestiaAccentLight = Color(0xFF22D3EE)   // cyan-400

// ===== Semantic Colors =====
val ProfitGreen = Color(0xFF10B981)        // emerald-500
val ProfitGreenLight = Color(0xFF34D399)   // emerald-400
val ProfitGreenDark = Color(0xFF059669)    // emerald-600
val ProfitGreenBg = Color(0x1A10B981)      // 10% opacity

val LossRed = Color(0xFFEF4444)            // red-500
val LossRedLight = Color(0xFFF87171)       // red-400
val LossRedDark = Color(0xFFDC2626)        // red-600
val LossRedBg = Color(0x1AEF4444)          // 10% opacity

val WarningOrange = Color(0xFFF59E0B)      // amber-500
val WarningOrangeLight = Color(0xFFFBBF24) // amber-400
val WarningOrangeBg = Color(0x1AF59E0B)    // 10% opacity

val NeutralGray = Color(0xFF94A3B8)        // slate-400

// ===== Signal Colors (from web: badge variants) =====
val SignalStrongBuy = Color(0xFF10B981)    // emerald
val SignalBuy = Color(0xFF34D399)          // emerald-400
val SignalHold = Color(0xFFF59E0B)         // amber
val SignalSell = Color(0xFFF87171)         // red-400
val SignalStrongSell = Color(0xFFEF4444)   // red-500

// ===== Score Colors =====
val ScoreExcellent = Color(0xFF10B981)     // 80+
val ScoreGood = Color(0xFF06B6D4)          // 60-79
val ScoreAverage = Color(0xFFF59E0B)       // 40-59
val ScorePoor = Color(0xFFEF4444)          // <40

// ===== Text Colors =====
val TextPrimary = Color(0xFFF8FAFC)        // slate-50
val TextSecondary = Color(0xFFCBD5E1)      // slate-300
val TextMuted = Color(0xFF94A3B8)          // slate-400
val TextDim = Color(0xFF64748B)            // slate-500

// ===== Gradient Colors =====
val GradientPrimaryStart = Color(0xFF6366F1)   // indigo
val GradientPrimaryMid = Color(0xFF8B5CF6)     // violet
val GradientPrimaryEnd = Color(0xFF06B6D4)     // cyan

val GradientSuccessStart = Color(0xFF10B981)   // emerald
val GradientSuccessEnd = Color(0xFF06B6D4)     // cyan

val GradientDangerStart = Color(0xFFEF4444)    // red
val GradientDangerEnd = Color(0xFFF59E0B)      // amber

val GradientCardStart = Color(0x120F172A)      // card gradient start
val GradientCardEnd = Color(0x0A1E293B)        // card gradient end

// ===== Glass Effect Colors =====
val GlassBg = Color(0xB30F172A)            // rgba(15,23,42,0.7)
val GlassBorder = Color(0x14FFFFFF)        // rgba(255,255,255,0.08)
val GlassHighlight = Color(0x0DFFFFFF)     // rgba(255,255,255,0.05)

// ===== Light theme (minimal support) =====
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
