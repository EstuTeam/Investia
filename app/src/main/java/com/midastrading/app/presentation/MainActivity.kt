package com.midastrading.app.presentation

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
import com.midastrading.app.data.local.ThemePreferences
import com.midastrading.app.presentation.navigation.MidasNavHost
import com.midastrading.app.presentation.theme.DarkBg
import com.midastrading.app.presentation.theme.LightBg
import com.midastrading.app.presentation.theme.MidasTheme
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

            MidasTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (isDarkTheme) DarkBg else LightBg
                ) {
                    MidasNavHost()
                }
            }
        }
    }
}
