package com.investia.app.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Debounce a value change to prevent excessive recompositions
 */
@Composable
fun <T> rememberDebounced(value: T, delayMs: Long = 300L): T {
    var debouncedValue by remember { mutableStateOf(value) }

    LaunchedEffect(value) {
        delay(delayMs)
        debouncedValue = value
    }

    return debouncedValue
}

/**
 * Throttle clicks to prevent double-tap issues
 */
class ThrottledClickHandler(private val intervalMs: Long = 500L) {
    private var lastClickTime = 0L

    fun onClick(action: () -> Unit) {
        val now = System.currentTimeMillis()
        if (now - lastClickTime >= intervalMs) {
            lastClickTime = now
            action()
        }
    }
}

@Composable
fun rememberThrottledClick(intervalMs: Long = 500L): ThrottledClickHandler {
    return remember { ThrottledClickHandler(intervalMs) }
}

/**
 * Extension to format timestamps in Turkish locale
 */
fun Long.toRelativeTime(): String {
    val diff = System.currentTimeMillis() - this
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Az önce"
        minutes < 60 -> "${minutes} dk önce"
        hours < 24 -> "${hours} saat önce"
        days < 7 -> "${days} gün önce"
        days < 30 -> "${days / 7} hafta önce"
        else -> "${days / 30} ay önce"
    }
}

/**
 * Format number with K/M/B suffix
 */
fun Long.toCompactString(): String {
    return when {
        this >= 1_000_000_000 -> String.format("%.1fB", this / 1_000_000_000.0)
        this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format("%.1fK", this / 1_000.0)
        else -> this.toString()
    }
}
