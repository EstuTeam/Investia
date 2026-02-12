package com.investia.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InvestiaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.d("InvestiaApp", "Firebase initialized")

        // Configure Crashlytics
        FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            setCustomKey("app_variant", BuildConfig.BUILD_TYPE)
        }

        // Configure Performance Monitoring
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = !BuildConfig.DEBUG
    }
}
