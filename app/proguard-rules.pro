# ============================================================
# Investia - ProGuard / R8 Optimization Rules
# ============================================================

# General
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================================
# Retrofit
# ============================================================
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okio.**
-dontwarn okhttp3.**

# ============================================================
# OkHttp
# ============================================================
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn javax.annotation.**

# ============================================================
# Gson
# ============================================================
-keep class com.investia.app.data.remote.dto.** { *; }
-keep class com.investia.app.domain.model.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================================
# Coroutines
# ============================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ============================================================
# Kotlin Serialization
# ============================================================
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# ============================================================
# Hilt / Dagger
# ============================================================
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-dontwarn dagger.**

# ============================================================
# Compose
# ============================================================
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# ============================================================
# DataStore
# ============================================================
-keep class androidx.datastore.** { *; }

# ============================================================
# Vico Chart Library
# ============================================================
-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**

# ============================================================
# Coil
# ============================================================
-keep class coil.** { *; }
-dontwarn coil.**

# ============================================================
# Remove logging in release
# ============================================================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
}

# ============================================================
# Enum optimization
# ============================================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================================
# Parcelable
# ============================================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}
