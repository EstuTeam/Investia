package com.investia.app.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Biometric authentication helper.
 * Supports fingerprint and face recognition.
 */
@Singleton
class BiometricHelper @Inject constructor() {

    /**
     * Check if biometric auth is available on the device.
     */
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Show biometric prompt and return result.
     * Must be called from a FragmentActivity.
     */
    suspend fun authenticate(
        activity: FragmentActivity,
        title: String = "Kimlik Doğrulama",
        subtitle: String = "Devam etmek için parmak izinizi kullanın",
        negativeButtonText: String = "İptal"
    ): BiometricResult = suspendCancellableCoroutine { continuation ->
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Success)
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Error(errorCode, errString.toString()))
                }
            }

            override fun onAuthenticationFailed() {
                // Called on each failed attempt, but prompt stays open.
                // Only resume on final error or success.
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)

        continuation.invokeOnCancellation {
            biometricPrompt.cancelAuthentication()
        }
    }
}

sealed class BiometricResult {
    data object Success : BiometricResult()
    data class Error(val errorCode: Int, val message: String) : BiometricResult()
}
