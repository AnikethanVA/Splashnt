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

**Pexels (optional):** A Pexels source is wired into the data layer for the upcoming multi-source support. It isn't reachable from the UI yet, so no key is needed to build or run today. To exercise it later, add a free [Pexels API key](https://www.pexels.com/api/) to `local.properties` (git-ignored):

```properties
PEXELS_API_KEY=your_key_here
```

**Requirements:** Android Studio, minSdk 31 (Android 12+), targetSdk 36

## Architecture

Single-module MVVM app with clean separation between data and UI layers.

```
app/src/main/java/com/ava/splashnt/
├── data/
│   ├── model/          # Domain types (Wallpaper, Topic) + per-provider DTOs (Unsplash*, Pexels*)
│   ├── mapper/         # DTO → domain extension functions (UnsplashMapper, PexelsMapper)
│   ├── remote/         # Shared Ktor client factory + per-provider API clients (Unsplash, Pexels)
│   └── repository/     # WallpaperRepository interface + Unsplash/Pexels impls, picked by WallpaperRepositoryProvider
├── di/                 # Koin dependency injection modules
└── ui/
    ├── home/           # Staggered grid + topic chip row + ViewModel with pagination
    ├── detail/         # Full-screen viewer with gestures + overlays
    ├── common/         # Reusable composables (CenteredLoader, SpringyTextButton)
    └── theme/          # Material You theming
```

See [`docs/architecture.md`](docs/architecture.md) for Mermaid class/flow diagrams of the data layer, UI state model, and navigation (renders inline on GitHub).

## Roadmap

- **Provider abstraction & additional sources** — Provider-neutral domain models (`Wallpaper`, `Topic`) extracted, and Pexels wired in as a sibling `PexelsWallpaperRepository` (data layer complete); next is a navigation drawer + source switching to surface it in the UI
- **Polish** — Search, wallpaper crop preview before applying (via `WallpaperManager.getCropAndSetWallpaperIntent`), collapsing header on scroll, home screen design polish (top bar, wordmark subtitle), shared element transitions

## License

This project is for educational purposes.