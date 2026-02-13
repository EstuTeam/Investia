package com.investia.app.di

import android.content.Context
import com.investia.app.BuildConfig
import com.investia.app.data.local.TokenManager
import com.investia.app.data.remote.InvestiaApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Auth interceptor — reads cached token synchronously (no runBlocking).
     * Token is cached in-memory in TokenManager.cachedToken.
     */
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthInterceptor(tokenManager: TokenManager): Interceptor {
        return Interceptor { chain ->
            val token = tokenManager.cachedToken
            val request = chain.request().newBuilder().apply {
                addHeader("Content-Type", "application/json")
                addHeader("Accept", "application/json")
                addHeader("X-Client", "investia-android/1.0.0")
                if (!token.isNullOrBlank()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }.build()
            chain.proceed(request)
        }
    }

    /**
     * Token refresh authenticator — automatically refreshes JWT on 401.
     * Uses the refresh token to get a new access token from /api/auth/refresh.
     */
    @Provides
    @Singleton
    fun provideTokenAuthenticator(tokenManager: TokenManager): Authenticator {
        return Authenticator { _, response ->
            // Don't retry if we've already tried refreshing (prevent infinite loop)
            if (response.request.header("X-Retry-Auth") != null) {
                return@Authenticator null
            }

            // Don't try to refresh for auth endpoints themselves
            val path = response.request.url.encodedPath
            if (path.contains("/api/auth/login") || path.contains("/api/auth/register") || path.contains("/api/auth/refresh")) {
                return@Authenticator null
            }

            val refreshToken = tokenManager.cachedRefreshToken ?: return@Authenticator null

            // Build refresh request synchronously
            val refreshRequest = okhttp3.Request.Builder()
                .url(BuildConfig.API_BASE_URL + "/api/auth/refresh")
                .post(
                    """{"refresh_token":"$refreshToken"}"""
                        .toRequestBody("application/json".toMediaTypeOrNull())
                )
                .build()

            try {
                val refreshClient = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

                val refreshResponse = refreshClient.newCall(refreshRequest).execute()

                if (refreshResponse.isSuccessful) {
                    val body = refreshResponse.body?.string()
                    val json = com.google.gson.JsonParser.parseString(body).asJsonObject
                    val newToken = json.get("token")?.asString
                        ?: json.get("access_token")?.asString

                    if (!newToken.isNullOrBlank()) {
                        // Save new token (blocking is OK in Authenticator — it's on OkHttp thread)
                        kotlinx.coroutines.runBlocking {
                            tokenManager.saveToken(newToken)
                            // Save new refresh token if provided
                            val newRefresh = json.get("refresh_token")?.asString
                            if (!newRefresh.isNullOrBlank()) {
                                tokenManager.saveRefreshToken(newRefresh)
                            }
                        }

                        // Retry the original request with new token
                        return@Authenticator response.request.newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer $newToken")
                            .addHeader("X-Retry-Auth", "true")
                            .build()
                    }
                }

                // Refresh failed — clear tokens (session expired)
                kotlinx.coroutines.runBlocking { tokenManager.clearAll() }
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Retry interceptor with exponential backoff.
     * Retries on server errors (502/503/504) and IOExceptions (connection drops).
     */
    @Provides
    @Singleton
    @Named("retry")
    fun provideRetryInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            var response: okhttp3.Response? = null
            var lastException: IOException? = null

            var retryCount = 0
            val maxRetries = 3

            while (retryCount <= maxRetries) {
                try {
                    response?.close()
                    response = chain.proceed(request)

                    // If success or non-retryable error, return
                    if (response.isSuccessful || response.code !in listOf(502, 503, 504)) {
                        return@Interceptor response
                    }
                } catch (e: IOException) {
                    lastException = e
                    if (retryCount >= maxRetries) throw e
                }

                retryCount++
                // Exponential backoff: 1s, 2s, 4s
                try { Thread.sleep(1000L * retryCount) } catch (_: InterruptedException) {}
            }

            response ?: throw (lastException ?: IOException("Bağlantı kurulamadı"))
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        @Named("auth") authInterceptor: Interceptor,
        @Named("retry") retryInterceptor: Interceptor,
        authenticator: Authenticator
    ): OkHttpClient {
        // 10 MB HTTP cache for conditional requests
        val cacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(cacheDir, 10L * 1024 * 1024)

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(authInterceptor)
            .addInterceptor(retryInterceptor)
            .authenticator(authenticator)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL + "/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideInvestiaApiService(retrofit: Retrofit): InvestiaApiService {
        return retrofit.create(InvestiaApiService::class.java)
    }
}
