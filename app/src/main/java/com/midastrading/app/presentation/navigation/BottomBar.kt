package com.midastrading.app.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.midastrading.app.presentation.theme.*

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Dashboard.route,
        label = "Ana Sayfa",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Screen.DailyPicks.route,
        label = "Fırsatlar",
        selectedIcon = Icons.AutoMirrored.Filled.TrendingUp,
        unselectedIcon = Icons.AutoMirrored.Outlined.TrendingUp
    ),
    BottomNavItem(
        route = Screen.IPO.route,
        label = "Halka Arz",
        selectedIcon = Icons.Filled.Business,
        unselectedIcon = Icons.Outlined.Business
    ),
    BottomNavItem(
        route = Screen.Screener.route,
        label = "Tarama",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    BottomNavItem(
        route = Screen.Profile.route,
        label = "Daha Fazla",
        selectedIcon = Icons.Filled.MoreHoriz,
        unselectedIcon = Icons.Outlined.MoreHoriz
    )
)

@Composable
fun MidasBottomBar(
    navController: NavHostController,
    currentRoute: String?
) {
    // Glassmorphic bottom bar matching web design
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                )
            )
    ) {
        NavigationBar(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
            tonalElevation = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidasPrimary,
                        selectedTextColor = MidasPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MidasPrimary.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }
}
