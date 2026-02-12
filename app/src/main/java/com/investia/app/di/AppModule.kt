package com.investia.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.investia.app.data.local.CacheManager
import com.investia.app.data.local.NotificationHelper
import com.investia.app.data.local.ThemePreferences
import com.investia.app.data.local.TokenManager
import com.investia.app.data.remote.InvestiaApiService
import com.investia.app.data.remote.WebSocketService
import com.investia.app.data.repository.AIChatRepositoryImpl
import com.investia.app.data.repository.AlertRepositoryImpl
import com.investia.app.data.repository.AuthRepositoryImpl
import com.investia.app.data.repository.BacktestRepositoryImpl
import com.investia.app.data.repository.ChatRoomRepositoryImpl
import com.investia.app.data.repository.IPORepositoryImpl
import com.investia.app.data.repository.IndicatorRepositoryImpl
import com.investia.app.data.repository.MarketRepositoryImpl
import com.investia.app.data.repository.NewsRepositoryImpl
import com.investia.app.data.repository.PortfolioRepositoryImpl
import com.investia.app.domain.repository.AIChatRepository
import com.investia.app.domain.repository.AlertRepository
import com.investia.app.domain.repository.AuthRepository
import com.investia.app.domain.repository.BacktestRepository
import com.investia.app.domain.repository.ChatRoomRepository
import com.investia.app.domain.repository.IPORepository
import com.investia.app.domain.repository.IndicatorRepository
import com.investia.app.domain.repository.MarketRepository
import com.investia.app.domain.repository.NewsRepository
import com.investia.app.domain.repository.PortfolioRepository
import com.investia.app.util.ConnectivityMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "investia_prefs")

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
        api: InvestiaApiService,
        cache: CacheManager
    ): MarketRepository {
        return MarketRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: InvestiaApiService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(api, tokenManager)
    }

    @Provides
    @Singleton
    fun provideIPORepository(
        api: InvestiaApiService,
        cache: CacheManager
    ): IPORepository {
        return IPORepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun providePortfolioRepository(
        api: InvestiaApiService,
        cache: CacheManager
    ): PortfolioRepository {
        return PortfolioRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideAlertRepository(
        api: InvestiaApiService,
        cache: CacheManager
    ): AlertRepository {
        return AlertRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        api: InvestiaApiService,
        cache: CacheManager
    ): NewsRepository {
        return NewsRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideAIChatRepository(
        api: InvestiaApiService
    ): AIChatRepository {
        return AIChatRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideBacktestRepository(
        api: InvestiaApiService,
        cache: CacheManager
    ): BacktestRepository {
        return BacktestRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideIndicatorRepository(
        api: InvestiaApiService,
        cache: CacheManager
    ): IndicatorRepository {
        return IndicatorRepositoryImpl(api, cache)
    }

    @Provides
    @Singleton
    fun provideChatRoomRepository(
        api: InvestiaApiService,
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
