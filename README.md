# Splashnt

A wallpaper browser and setter app for Android, built with Jetpack Compose and powered by the [Unsplash API](https://unsplash.com/developers).

## Features

- **Browse wallpapers** — Staggered grid layout with infinite scroll pagination
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

1. Get a free API key from [Unsplash Developers](https://unsplash.com/developers)
2. Add it to `local.properties` in the project root:
   ```
   UNSPLASH_ACCESS_KEY=your_key_here
   ```
3. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```

**Requirements:** Android Studio, minSdk 30 (Android 11+), targetSdk 36

## Architecture

Single-module MVVM app with clean separation between data and UI layers.

```
app/src/main/java/com/ava/splashnt/
├── data/
│   ├── model/          # Unsplash API response models
│   ├── remote/         # Ktor HTTP client
│   └── repository/     # Repository interface + implementations
├── di/                 # Koin dependency injection modules
└── ui/
    ├── home/           # Staggered grid + ViewModel with pagination
    ├── detail/         # Full-screen viewer with gestures + overlays
    └── theme/          # Material You theming
```

## Roadmap

- **Additional Sources** — Pexels API integration, navigation drawer for source selection, multi-provider switching
- **Polish** — Pull-to-refresh, splash screen, categories/search filtering, dynamic theming, shared element transitions

## License

This project is for educational purposes.