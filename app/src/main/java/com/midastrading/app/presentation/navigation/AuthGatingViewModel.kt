package com.midastrading.app.presentation.navigation

import androidx.lifecycle.ViewModel
import com.midastrading.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Simple ViewModel to provide AuthRepository to MidasNavHost via Hilt injection.
 */
@HiltViewModel
class AuthGatingViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel()
