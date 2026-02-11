# Midas Trading - Android App

## 🏗️ Mimari
- **Kotlin** + **Jetpack Compose** (Material 3)
- **Clean Architecture** (Data → Domain → Presentation)
- **MVVM** pattern with StateFlow
- **Hilt** dependency injection
- **Retrofit** + OkHttp for networking
- **Vico** for charts

## 📁 Proje Yapısı
```
com.midastrading.app/
├── MidasApp.kt                      # Application class
├── di/                               # Dependency Injection
│   ├── AppModule.kt
│   └── NetworkModule.kt
├── data/
│   ├── local/TokenManager.kt        # DataStore auth token
│   ├── remote/
│   │   ├── MidasApiService.kt       # Retrofit API
│   │   └── dto/Dtos.kt              # API response models
│   └── repository/                   # Repository implementations
├── domain/
│   ├── model/                        # Domain models
│   └── repository/                   # Repository interfaces
├── presentation/
│   ├── MainActivity.kt
│   ├── theme/                        # Material 3 theme
│   ├── navigation/                   # Navigation graph + Bottom bar
│   ├── components/                   # Reusable UI components
│   └── screens/
│       ├── dashboard/                # Ana sayfa
│       ├── dailypicks/               # Günün fırsatları
│       ├── portfolio/                # Portföy
│       ├── screener/                 # Hisse tarama
│       ├── stockdetail/              # Hisse detay
│       ├── signals/                  # Sinyal merkezi
│       ├── news/                     # Haberler
│       ├── calculator/               # Hesaplayıcı
│       ├── chat/                     # AI Asistan
│       ├── auth/                     # Login/Register
│       └── profile/                  # Profil
└── util/                             # Formatters, Resource wrapper
```

## 🚀 Build & Run

### Gereksinimler
- Android Studio Hedgehog (2023.1.1) veya üstü
- JDK 17
- Android SDK 34
- Kotlin 1.9.22

### Adımlar
1. Android Studio'da `mobile/` klasörünü aç
2. Gradle sync tamamlansın
3. Emulator veya fiziksel cihazda çalıştır

### API Bağlantısı
- **Debug**: `http://10.0.2.2:8000` (emulator → localhost)
- **Release**: `https://trading-botu.vercel.app`

## 📱 Ekranlar
| Ekran | Durum |
|-------|-------|
| Dashboard | ✅ Hazır |
| Günün Fırsatları | ✅ Hazır |
| Portföy | ✅ Hazır |
| Hisse Tarama | ✅ Hazır |
| Hisse Detay | ✅ Hazır |
| Sinyal Merkezi | ✅ Hazır |
| Haberler | ✅ Hazır |
| Hesaplayıcı | ✅ Hazır |
| AI Asistan | ✅ Hazır |
| Login/Register | ✅ Hazır |
| Profil | ✅ Hazır |

## 🎨 Design System
- Material 3 + Custom Midas theme
- Dark/Light mode support
- Midas brand color: `#4959EA`
- Finance colors: Green (#10B981) / Red (#EF4444)
- Score badges, Signal chips, PnL text components
