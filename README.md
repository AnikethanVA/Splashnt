# Splashnt

A wallpaper browser and setter app for Android, built with Jetpack Compose and powered by Unsplash's unofficial internal API (`unsplash.com/napi/`) — no API key required.

## Features

- **Browse wallpapers** — Staggered grid layout with infinite scroll pagination and pull-to-refresh
- **Filter by category** — Chip row above the grid lets you scope the feed to Unsplash featured topics ("All" plus topics like Nature, Architecture, Travel, etc.)
- **Full-screen viewer** — Pinch-to-zoom (1x–5x), pan with clamped boundaries, double-tap to toggle fit/fill
- **Photographer details** — Tap to reveal an animated overlay with photographer name and profile link
- **Download** — Save images to device via DownloadManager with notification progress
- **Set as wallpaper** — Apply images to home screen, lock screen, or both via WallpaperManager
- **Material You** — Dynamic theming with Material 3 expressive components

## Tech Stack

| Layer | Libraries |
|-------|-----------|
| UI | Jetpack Compose, Material 3, Navigation 3 |
| Networking | Ktor (Android engine) |
| Image Loading | Coil 3 |
| Serialization | kotlinx.serialization |
| DI | Koin |

All libraries are chosen for **Kotlin Multiplatform** compatibility.

## Setup

No API key needed. Just build and run:

```bash
./gradlew assembleDebug
```

The app hits Unsplash's unofficial `napi` endpoint (the same one their website uses). Premium (Unsplash+) photos are filtered out at the repository layer since they aren't downloadable without a paid account.

**Requirements:** Android Studio, minSdk 31 (Android 12+), targetSdk 36

## Architecture

Single-module MVVM app with clean separation between data and UI layers.

```
app/src/main/java/com/ava/splashnt/
├── data/
│   ├── model/          # Domain types (Wallpaper, Topic) + Unsplash API DTOs (UnsplashModel, UnsplashTopic)
│   ├── mapper/         # DTO → domain extension functions (UnsplashMapper)
│   ├── remote/         # Ktor HTTP client
│   └── repository/     # Repository interface + implementations (returns domain types)
├── di/                 # Koin dependency injection modules
└── ui/
    ├── home/           # Staggered grid + topic chip row + ViewModel with pagination
    ├── detail/         # Full-screen viewer with gestures + overlays
    ├── common/         # Reusable composables (CenteredLoader, SpringyTextButton)
    └── theme/          # Material You theming
```

## Roadmap

- **Provider abstraction & additional sources** — Provider-neutral domain models (`Wallpaper`, `Topic`) extracted; next is adding Pexels alongside Unsplash with a navigation drawer for source selection
- **Polish** — Search, wallpaper crop preview before applying (via `WallpaperManager.getCropAndSetWallpaperIntent`), collapsing header on scroll, home screen design polish (top bar, wordmark subtitle), shared element transitions

## License

This project is for educational purposes.