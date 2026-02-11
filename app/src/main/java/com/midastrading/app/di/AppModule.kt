package com.midastrading.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.midastrading.app.data.local.CacheManager
import com.midastrading.app.data.local.NotificationHelper
import com.midastrading.app.data.local.ThemePreferences
import com.midastrading.app.data.local.TokenManager
import com.midastrading.app.data.remote.MidasApiService
import com.midastrading.app.data.remote.WebSocketService
import com.midastrading.app.data.repository.AIChatRepositoryImpl
import com.midastrading.app.data.repository.AlertRepositoryImpl
import com.midastrading.app.data.repository.AuthRepositoryImpl
import com.midastrading.app.data.repository.BacktestRepositoryImpl
import com.midastrading.app.data.repository.ChatRoomRepositoryImpl
import com.midastrading.app.data.repository.IPORepositoryImpl
import com.midastrading.app.data.repository.IndicatorRepositoryImpl
import com.midastrading.app.data.repository.MarketRepositoryImpl
import com.midastrading.app.data.repository.NewsRepositoryImpl
import com.midastrading.app.data.repository.PortfolioRepositoryImpl
import com.midastrading.app.domain.repository.AIChatRepository
import com.midastrading.app.domain.repository.AlertRepository
import com.midastrading.app.domain.repository.AuthRepository
import com.midastrading.app.domain.repository.BacktestRepository
import com.midastrading.app.domain.repository.ChatRoomRepository
import com.midastrading.app.domain.repository.IPORepository
import com.midastrading.app.domain.repository.IndicatorRepository
import com.midastrading.app.domain.repository.MarketRepository
import com.midastrading.app.domain.repository.NewsRepository
import com.midastrading.app.domain.repository.PortfolioRepository
import com.midastrading.app.util.ConnectivityMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "midas_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideCacheManager(): CacheManager {
        return CacheManager()
    }

    @Provides
    @Singleton
    fun provideConnectivityMonitor(@ApplicationContext context: Context): ConnectivityMonitor {
        return ConnectivityMonitor(context)
    }

    @Provides
    @Singleton
    fun provideMarketRepository(
        api: MidasApiService,
        cache: CacheManager
    ): MarketRepository {
        return MarketRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: MidasApiService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(api, tokenManager)
    }

    @Provides
    @Singleton
    fun provideIPORepository(
        api: MidasApiService,
        cache: CacheManager
    ): IPORepository {
        return IPORepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun providePortfolioRepository(
        api: MidasApiService,
        cache: CacheManager
    ): PortfolioRepository {
        return PortfolioRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideAlertRepository(
        api: MidasApiService,
        cache: CacheManager
    ): AlertRepository {
        return AlertRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        api: MidasApiService,
        cache: CacheManager
    ): NewsRepository {
        return NewsRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideAIChatRepository(
        api: MidasApiService
    ): AIChatRepository {
        return AIChatRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideBacktestRepository(
        api: MidasApiService,
        cache: CacheManager
    ): BacktestRepository {
        return BacktestRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideIndicatorRepository(
        api: MidasApiService,
        cache: CacheManager
    ): IndicatorRepository {
        return IndicatorRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideChatRoomRepository(
        api: MidasApiService,
        cache: CacheManager
    ): ChatRoomRepository {
        return ChatRoomRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideThemePreferences(dataStore: DataStore<Preferences>): ThemePreferences {
        return ThemePreferences(dataStore)
    }

    @Provides
    @Singleton
    fun provideWebSocketService(
        client: OkHttpClient,
        tokenManager: TokenManager
    ): WebSocketService {
        return WebSocketService(client, tokenManager)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }
}
