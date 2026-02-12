package com.investia.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_DARK_THEME] ?: true // Default dark
    }

    suspend fun toggleTheme() {
        dataStore.edit { prefs ->
            prefs[IS_DARK_THEME] = !(prefs[IS_DARK_THEME] ?: true)
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_DARK_THEME] = isDark
        }
    }
}
