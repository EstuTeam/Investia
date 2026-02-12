package com.investia.app.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investia.app.data.local.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val isDarkTheme: Flow<Boolean> = themePreferences.isDarkTheme

    fun toggleTheme() {
        viewModelScope.launch {
            themePreferences.toggleTheme()
        }
    }
}
