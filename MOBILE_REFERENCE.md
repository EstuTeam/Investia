# 📱 Investia - Mobil Uygulama Referans Belgesi

> Web versiyonundan alınan kritik bilgiler ve mobil uygulama mevcut durumu

---

## 🌐 WEB VERSİYONUNDAN KRİTİK BİLGİLER

### API Base URL Yapılandırması
- **Production:** `https://trading-botu.vercel.app`
- **Debug (Emulator):** `http://10.0.2.2:8000`
- **Auth Header:** `Bearer <JWT_TOKEN>`
- **Token yenileme:** `/api/auth/refresh` (POST, refresh_token body)

---

### 📡 TÜM API ENDPOINT'LERİ

#### 🔐 Auth (`/api/auth/`)
| Method | Path | Açıklama | Auth |
|--------|------|----------|:----:|
| POST | `/api/auth/register` | Kayıt (email, password, full_name) | ❌ |
| POST | `/api/auth/login` | Giriş → JWT + refresh token | ❌ |
| POST | `/api/auth/refresh` | Token yenileme | ❌ |
| GET | `/api/auth/me` | Mevcut kullanıcı profili | ✅ |
| PUT | `/api/auth/me` | Profil güncelle | ✅ |
| POST | `/api/auth/change-password` | Şifre değiştir | ✅ |
| POST | `/api/auth/logout` | Çıkış | ✅ |
| GET | `/api/auth/verify` | Token doğrulama | ✅ |

#### 📈 Stocks (`/api/stocks/`)
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/stocks/{symbol}/data` | OHLCV fiyat verisi (interval, period params) |
| GET | `/api/stocks/{symbol}/indicators` | Teknik indikatörler (RSI, MACD vb.) |
| GET | `/api/stocks/{symbol}/info` | Hisse bilgisi |
| GET | `/api/stocks/{symbol}/current-price` | Anlık fiyat |
| GET | `/api/stocks/debug-yfinance` | Debug yfinance |

#### 📡 Signals (`/api/signals/`)
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/market-status` | Piyasa durumu |
| GET | `/api/signals/daily-picks` | **Ana günlük seçimler** (V2+V3 Hybrid, max 5) |
| GET | `/api/signals/daily-picks/history` | Geçmiş seçimler (30 güne kadar) |
| GET | `/api/signals/{symbol}` | Belirli hisse sinyali |
| GET | `/api/signals/calculator/position-size` | Pozisyon hesaplayıcı |
| POST | `/api/signals/daily-picks/refresh` | Manuel tarama tetikle |

#### 🔍 Screener (`/api/screener/`)
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/screener/top-picks` | Günlük en iyi seçimler (momentum skoru) |
| GET | `/api/screener/signal/{symbol}` | Gerçek zamanlı hisse sinyali |
| GET | `/api/screener/scan` | Tüm BIST30 taraması |
| GET | `/api/screener/top-movers` | En çok kazanan/kaybedenler |
| GET | `/api/screener/morning-picks` | Sabah 5'li strateji |
| GET | `/api/screener/market-timing` | Piyasa fazı & zamanlama |

#### 📊 Indicators (`/api/indicators/`)
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/indicators/{symbol}/ichimoku` | Ichimoku Cloud |
| GET | `/api/indicators/{symbol}/fibonacci` | Fibonacci düzeltme seviyeleri |
| GET | `/api/indicators/{symbol}/bollinger` | Bollinger Bantları |
| GET | `/api/indicators/{symbol}/trend-channel` | Trend kanalı + sinyaller |

#### 🧪 Backtest (`/api/backtest/`)
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/backtest/daily/{days}` | Günlük strateji backtesti |
| GET | `/api/backtest/quick` | Hızlı 30 günlük backtest |

#### 🔔 Alerts (`/api/alerts/`)
| Method | Path | Açıklama |
|--------|------|----------|
| POST | `/api/alerts/create` | Alert oluştur (price/score/signal) |
| GET | `/api/alerts/active` | Aktif alertleri getir |
| GET | `/api/alerts/check` | Tetiklenen alertleri kontrol et |
| DELETE | `/api/alerts/{id}` | Alert sil |
| PUT | `/api/alerts/{id}/toggle` | Alert aç/kapat |
| GET | `/api/alerts/notifications/history` | Bildirim geçmişi |
| PUT | `/api/alerts/notifications/{id}/read` | Bildirim okundu |
| PUT | `/api/alerts/notifications/read-all` | Tümü okundu |
| DELETE | `/api/alerts/notifications/clear` | Bildirimleri temizle |
| GET | `/api/alerts/stats` | Alert istatistikleri |

#### 📰 News (`/api/news/`)
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/news/economy` | Ekonomi haberleri (TR + Dünya RSS) |
| GET | `/api/news/general` | Gündem haberleri |

#### 💬 Chat (`/api/chat/`)
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/chat/rooms` | Sohbet odaları listesi |
| GET | `/api/chat/rooms/{id}` | Oda detayı |
| GET | `/api/chat/rooms/{id}/messages` | Mesajlar (limit, before) |
| POST | `/api/chat/rooms/{id}/messages` | Mesaj gönder |
| POST | `/api/chat/rooms/{id}/messages/{mid}/reactions` | Tepki ekle |

#### 🏢 IPO (`/api/ipo/`)
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/ipo/all` | Tüm halka arzlar (status, sector filtre) |
| GET | `/api/ipo/stats` | İstatistikler |
| GET | `/api/ipo/active` | Aktif halka arzlar |
| GET | `/api/ipo/upcoming` | Yaklaşan halka arzlar |
| GET | `/api/ipo/{symbol}` | Detay |
| POST | `/api/ipo/calculator` | Yatırım hesaplayıcı |

#### 🤖 AI Assistant (`/api/ai/`)
| Method | Path | Açıklama |
|--------|------|----------|
| POST | `/api/ai/chat` | AI ile sohbet (Claude API) |
| POST | `/api/ai/analyze-portfolio` | Portföy analizi |
| POST | `/api/ai/analyze-trade` | İşlem analizi |
| GET | `/api/ai/market-summary` | Piyasa özeti |
| GET | `/api/ai/suggestions` | Önerilen sorular |
| GET | `/api/ai/quick-actions` | Hızlı aksiyonlar |
| GET | `/api/ai/history` | Sohbet geçmişi |
| DELETE | `/api/ai/history` | Geçmişi temizle |
| GET | `/api/ai/stock-analysis/{symbol}` | Hisse analizi |

#### 🌍 Market Data (`/api/market/`)
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/market/overview` | Tüm piyasa (BIST, döviz, emtia, global) |
| GET | `/api/market/forex` | USD/TRY, EUR/TRY |
| GET | `/api/market/commodities` | Altın, Bitcoin |
| GET | `/api/market/global` | S&P500, NASDAQ |

#### 💼 Portfolio (`/api/portfolio/`) — Auth gerekli
| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/portfolio/` | Tüm portföyler |
| POST | `/api/portfolio/` | Portföy oluştur |
| GET | `/api/portfolio/{id}` | Portföy detayı |
| DELETE | `/api/portfolio/{id}` | Portföy sil |
| POST | `/api/portfolio/{id}/transactions` | İşlem ekle |
| DELETE | `/api/portfolio/{id}/transactions/{tid}` | İşlem sil |
| GET | `/api/portfolio/watchlists/all` | Takip listeleri |
| POST | `/api/portfolio/watchlists` | Takip listesi oluştur |
| POST | `/api/portfolio/watchlists/{id}/add` | Hisse ekle |
| DELETE | `/api/portfolio/watchlists/{id}/remove/{ticker}` | Hisse çıkar |

---

### 📊 BIST30 HİSSE LİSTESİ & SEKTÖR HARİTASI

```
AKBNK.IS — Bankacılık       AKSEN.IS — Enerji         ARCLK.IS — Dayanıklı Tüketim
ASELS.IS — Savunma          BIMAS.IS — Perakende       EKGYO.IS — GYO
ENKAI.IS — İnşaat           EREGL.IS — Demir Çelik     FROTO.IS — Otomotiv
GARAN.IS — Bankacılık       GUBRF.IS — Kimya           HEKTS.IS — Kimya
ISCTR.IS — Bankacılık       KCHOL.IS — Holding         KRDMD.IS — Demir Çelik
ODAS.IS  — Enerji           PETKM.IS — Petrokimya      PGSUS.IS — Havacılık
SAHOL.IS — Holding          SASA.IS  — Petrokimya      SISE.IS  — Cam
TAVHL.IS — Havacılık        TCELL.IS — Telekomünikasyon THYAO.IS — Havacılık
TKFEN.IS — Holding          TOASO.IS — Otomotiv         TUPRS.IS — Enerji
YKBNK.IS — Bankacılık
```

**Popüler hisseler:** TRALT.IS, THYAO.IS, GARAN.IS, AKBNK.IS, ASELS.IS, TUPRS.IS

---

### 🎯 STRATEJİ BİLGİLERİ (V2+V3 Hybrid)

- **Min skor:** 75 (V2 kalite filtresi)
- **Piyasa filtresi:** BIST100 yükseliş trendi kontrolü
- **Sektör çeşitlendirmesi:** Sektör başına max 1 seçim
- **TP1:** 1:2.5 risk/ödül → %50 pozisyon kapat
- **TP2:** 1:4.0 risk/ödül → kalan %50 kapat
- **Beklenen kazanma oranı:** %62-70
- **İşlem başı risk:** Portföyün max %2-3

---

### 🔔 BİLDİRİM SİSTEMİ

- **Polling:** Her 5 dk'da `/api/alerts/check` çağrısı
- **Alert tipleri:** price_above/below, score_above, signal_change
- **Öncelik seviyeleri:** low, medium, high, critical
- **Kanallar:** Toast, Browser Notification, Ses
- **Yönetim:** Okundu işaretle, tümü okundu, geçmiş temizle

---

### 🌐 WebSocket Kanalları

| Endpoint | Amaç |
|----------|------|
| `ws://host/ws/stream` | Ana multiplexed stream (price, signal, alert, notification, screener) |
| `/ws/{ticker}` | Hisse başına gerçek zamanlı veri (3sn) |
| `/ws/signals/{ticker}` | Sinyal güncellemeleri (5sn) |
| `/ws/notifications/{user_id}` | Kullanıcı bildirimleri |

---

## 📱 MOBİL UYGULAMA MEVCUT DURUMU

### ✅ Tamamlanan Yapılar

#### Mimari (Clean Architecture + MVVM)
- **DI:** Hilt (Dagger) — `NetworkModule`, `AppModule`
- **Network:** Retrofit + OkHttp (auth interceptor, retry interceptor, logging)
- **Cache:** In-memory `CacheManager` (TTL bazlı)
- **Token:** DataStore (`TokenManager`)
- **Connectivity:** `ConnectivityMonitor`

#### Veri Katmanı
- **DTOs:** Tüm API response modelleri (`Dtos.kt`)
- **Domain Models:** StockPick, StockQuote, DailyPicksResponse, PortfolioItem, NewsItem, SignalData, MarketOverview, ChatMessage, AuthUser
- **Repositories:** `MarketRepository`, `AuthRepository` (interface + impl)
- **API Service:** `InvestiaApiService` (Retrofit interface)

#### Ekranlar (Compose)
| Ekran | Dosya | Durum |
|-------|-------|-------|
| Dashboard | `DashboardScreen.kt` + ViewModel | ✅ Aktif |
| Daily Picks | `DailyPicksScreen.kt` | ✅ Aktif |
| Portfolio | `PortfolioScreen.kt` | ✅ Aktif |
| Screener | `ScreenerScreen.kt` | ✅ Aktif |
| Profile | `ProfileScreen.kt` | ✅ Aktif |
| Stock Detail | `StockDetailScreen.kt` (symbol arg) | ✅ Aktif |
| Signal Center | `SignalCenterScreen.kt` | ✅ Aktif |
| News | `NewsScreen.kt` | ✅ Aktif |
| Calculator | `CalculatorScreen.kt` | ✅ Aktif |
| AI Chat | `AIChatScreen.kt` | ✅ Aktif |
| Login | `LoginScreen.kt` | ✅ Aktif |
| Register | `RegisterScreen.kt` | ✅ Aktif |

#### Navigation
- **Bottom Bar:** Dashboard, DailyPicks, Portfolio, Screener, Profile (5 tab)
- **Sub Screens:** StockDetail, SignalCenter, News, Calculator, AIChat
- **Auth:** Login, Register, ForgotPassword

#### Tema
- **Dark Mode** (varsayılan, web ile aynı)
- Light Mode desteği hazır
- Material3 color scheme
- Custom renk paleti: InvestiaPrimary, InvestiaSecondary, GainGreen, LossRed vb.

#### Build
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34
- **Compose BOM:** 2024.01.00
- **Kotlin:** kapt (Hilt için)
- **Charts:** Vico library
- **Image:** Coil
- **Son build:** ✅ BAŞARILI (warnings mevcut ama error yok)

---

### ❌ EKSİK OLAN ÖZELLİKLER (Web'de var, Mobilde yok)

#### Yüksek Öncelik
1. **IPO Sayfası** — Halka arz takibi (web'de tam sayfa var)
2. **Ekonomi/Gündem Haberleri ayrımı** — Mobilde tek News ekranı var
3. **Backtest** — Strateji test sonuçları görüntüleme
4. **Portfolio tam CRUD** — İşlem ekleme/silme, çoklu portföy, watchlist
5. **Alert sistemi** — Alert oluşturma, yönetme, bildirim alma
6. **Forgot Password** — Ekran tanımlı ama implement edilmemiş

#### Orta Öncelik
7. **WebSocket gerçek zamanlı veri** — Anlık fiyat güncellemeleri
8. **Performance sayfası** — İşlem geçmişi ve PnL takibi
9. **Ichimoku / Fibonacci / Bollinger / Trend Channel** — Gelişmiş indikatörler
10. **Push Notifications** — Firebase FCM entegrasyonu
11. **Grafik (candlestick)** — StockDetail'de mum grafik
12. **DailyPicks geçmişi** — Geçmiş günlerin seçimleri

#### Düşük Öncelik
13. **Chat odaları** — Trader topluluğu sohbet
14. **AI portföy/işlem analizi** — Gelişmiş AI özellikleri
15. **Tema geçişi** — Dark/Light switch (UI yok, altyapı hazır)
16. **Pull to refresh** — Bazı ekranlarda eksik olabilir
17. **Offline mode** — Cache daha da iyileştirilebilir

---

### 🔧 BİLİNEN SORUNLAR

1. **Deprecation uyarıları:** `Icons.Filled.ArrowBack`, `TrendingUp`, `Send`, vb. → `Icons.AutoMirrored` kullanılmalı
2. **API endpoint uyumsuzlukları:** Mobil API service'teki bazı path'ler backend ile tam eşleşmeyebilir
3. **Auth response format:** Backend `data.access_token` dönerken mobil DTO `token` bekliyor — doğrulama gerekli
4. **Emulator bağlantısı:** `10.0.2.2:8000` debug mode'da backend çalışıyor olmalı

---

### 📂 MOBİL PROJE YAPISI

```
mobile/app/src/main/java/com/investiatrading/app/
├── InvestiaApp.kt                          # Application class (Hilt)
├── data/
│   ├── local/
│   │   ├── CacheManager.kt             # In-memory TTL cache
│   │   └── TokenManager.kt             # DataStore token yönetimi
│   ├── remote/
│   │   ├── InvestiaApiService.kt          # Retrofit API interface
│   │   └── dto/
│   │       └── Dtos.kt                 # Tüm DTO sınıfları
│   └── repository/
│       ├── AuthRepositoryImpl.kt       # Auth işlemleri impl
│       └── MarketRepositoryImpl.kt     # Market verileri impl
├── di/
│   ├── AppModule.kt                    # Hilt DI modülü (DataStore, repos)
│   └── NetworkModule.kt               # Retrofit, OkHttp, interceptors
├── domain/
│   ├── model/
│   │   ├── Auth.kt                    # AuthUser, LoginRequest, AuthResponse
│   │   └── Models.kt                  # StockPick, StockQuote, NewsItem, vb.
│   └── repository/
│       └── Repositories.kt           # MarketRepository, AuthRepository interfaces
├── presentation/
│   ├── MainActivity.kt
│   ├── components/
│   │   └── CommonComponents.kt       # Paylaşılan UI bileşenleri
│   ├── navigation/
│   │   ├── BottomBar.kt              # Alt navigasyon
│   │   ├── InvestiaNavHost.kt           # Tüm navigasyon
│   │   └── Screen.kt                 # Route tanımları
│   ├── screens/
│   │   ├── auth/         → LoginScreen, RegisterScreen
│   │   ├── calculator/   → CalculatorScreen
│   │   ├── chat/         → AIChatScreen
│   │   ├── dailypicks/   → DailyPicksScreen
│   │   ├── dashboard/    → DashboardScreen, DashboardViewModel, DashboardState
│   │   ├── news/         → NewsScreen
│   │   ├── portfolio/    → PortfolioScreen
│   │   ├── profile/      → ProfileScreen
│   │   ├── screener/     → ScreenerScreen
│   │   ├── signals/      → SignalCenterScreen
│   │   └── stockdetail/  → StockDetailScreen
│   └── theme/
│       ├── Color.kt                  # Renk paleti
│       ├── Theme.kt                  # Material3 tema
│       └── Type.kt                   # Tipografi
└── util/
    ├── ConnectivityMonitor.kt        # Ağ durumu izleme
    ├── Extensions.kt                 # Extension fonksiyonlar
    ├── Formatters.kt                 # Para, tarih format
    ├── NetworkResult.kt              # API sonuç wrapper
    └── Resource.kt                   # Resource sealed class
```

---

### 🎨 RENK PALETİ (Web ↔ Mobil eşleştirme)

| Amaç | Web CSS | Mobil Kotlin |
|------|---------|--------------|
| Ana arka plan | `#0F1019` | `DarkBg` |
| Surface | `#161827` | `DarkSurface` |
| Primary | `#6C63FF` | `InvestiaPrimary` |
| Yeşil (kazanç) | `#00C853` | `GainGreen` |
| Kırmızı (kayıp) | `#FF1744` | `LossRed` |
| Ana metin | `#F1F5F9` | `TextPrimary` |
| İkincil metin | `#94A3B8` | `TextSecondary` |
| Soluk metin | `#64748B` | `TextMuted` |
| Border | `#1E293B` | `DarkBorder` |

---

### 🚀 SONRAKİ ADIMLAR (Önerilen Geliştirme Sırası)

1. **API endpoint uyumsuzluklarını düzelt** — Backend ile mobil DTO'ları eşle
2. **Auth flow'u test et** — Login/Register → token kaydetme → authenticated istekler
3. **IPO sayfasını ekle** — Web'deki gibi halka arz takibi
4. **Alert sistemi ekle** — Bildirim oluşturma ve push notification
5. **Grafikleri zenginleştir** — Candlestick, indikatör overlay
6. **Performance sayfası** — İşlem geçmişi ve PnL
7. **WebSocket entegrasyonu** — Gerçek zamanlı fiyat güncellemesi
8. **Push Notifications** — Firebase FCM
9. **Offline mode iyileştirme** — Room DB ile kalıcı cache
10. **Widget** — Ana ekran stock widget'ı
