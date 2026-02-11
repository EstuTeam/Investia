import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
}

// Load signing credentials from local.properties (not in version control)
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

android {
    namespace = "com.midastrading.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.midastrading.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // API Base URL - production Vercel
        buildConfigField("String", "API_BASE_URL", "\"https://trading-botu.vercel.app\"")
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("RELEASE_STORE_FILE", "../midas-release-key.jks"))
            storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD", "")
            keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS", "")
            keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD", "")
        }
    }

    buildTypes {
        debug {
            // Telefon için production URL; emulator için http://10.0.2.2:8000 yapın
            buildConfigField("String", "API_BASE_URL", "\"https://trading-botu.vercel.app\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose BOM - align all compose dependency versions
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.animation:animation-core")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Serialization (Gson only - kotlinx-serialization removed as unused)
    implementation("com.google.code.gson:gson:2.10.1")

    // Security - Encrypted SharedPreferences for token storage
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // DataStore (local prefs)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // WorkManager for background notification polling
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
