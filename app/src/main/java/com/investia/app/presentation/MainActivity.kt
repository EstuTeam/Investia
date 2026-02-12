package com.investia.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.investia.app.data.local.ThemePreferences
import com.investia.app.presentation.navigation.InvestiaNavHost
import com.investia.app.presentation.theme.DarkBg
import com.investia.app.presentation.theme.LightBg
import com.investia.app.presentation.theme.InvestiaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = true)

            InvestiaTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (isDarkTheme) DarkBg else LightBg
                ) {
                    InvestiaNavHost()
                }
            }
        }
    }
}
