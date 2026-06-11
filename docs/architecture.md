# Splashnt — Architecture Diagrams

> Visual companion to `CLAUDE.md`. Mermaid renders natively on GitHub and in most
> IDE Markdown previews. Diagrams describe the structure as of 2026-06-11 (source
> picker complete). Keep them in sync when the data/UI layers change.

Splashnt is a single-module (`:app`) MVVM Android app. Requests flow **UI → ViewModel
→ Repository (selected by source) → API client → network**, and responses flow back as
**wire DTOs → mappers → provider-neutral domain types → UI state**.

---

## 1. Layered overview

The big picture: which layer talks to which, and where the two providers diverge.
Koin (`di/KoinModule.kt`) constructs every box below and injects the arrows.

```mermaid
flowchart TD
    subgraph UI["UI layer — ui/"]
        HS["HomeScreen<br/>(+ TopicChipRow, SourcePill,<br/>SourcePickerSheet)"]
        DS["DetailsScreen"]
    end

    subgraph PRES["Presentation — ui/home/"]
        HVM["HomeViewModel"]
        STATE["WallpaperUIState /<br/>ContentState / FeedSelection"]
    end

    subgraph REPO["Repository — data/repository/"]
        PROV["WallpaperRepositoryProvider"]
        IREPO{{"WallpaperRepository<br/>(interface)"}}
        UREPO["UnsplashWallpaperRepository"]
        PREPO["PexelsWallpaperRepository"]
    end

    subgraph REMOTE["Remote — data/remote/"]
        FAC(["buildWallpaperHttpClient<br/>(shared Ktor factory)"])
        UAPI["UnsplashApiClient"]
        PAPI["PexelsApiClient"]
    end

    subgraph NET["Network"]
        UN[("unsplash.com/napi")]
        PX[("api.pexels.com/v1")]
    end

    DOMAIN["Domain types — data/model/<br/>Wallpaper · WallpaperUrls · Topic"]

    HS --> HVM
    HS -->|"push DetailsScreen(Wallpaper)"| DS
    HVM --> STATE
    HVM --> PROV
    PROV -->|"when(WallpaperSource)"| IREPO
    IREPO -.->|implemented by| UREPO
    IREPO -.->|implemented by| PREPO
    UREPO --> UAPI
    PREPO --> PAPI
    UAPI --> FAC
    PAPI --> FAC
    UAPI --> UN
    PAPI --> PX
    UREPO -->|maps to| DOMAIN
    PREPO -->|maps to| DOMAIN
    DOMAIN --> STATE
```

> **Note:** Source switching is user-facing as of 2026-06-11: the `SourcePill` in the
> Home header opens `SourcePickerSheet` (a `ModalBottomSheet`), which calls
> `HomeViewModel.onProviderChanged(WallpaperSource)` — that resets to page 1 / feed
> "All", sets `Loading`, and re-fetches both photos **and** topics for the new provider.
> The active source is exposed as `currentWallpaperSource: StateFlow<WallpaperSource>`.

---

## 2. Data layer

### 2a. Repositories & the provider (Strategy pattern)

`WallpaperRepositoryProvider` picks one of two interchangeable `WallpaperRepository`
implementations by a `WallpaperSource` enum — textbook Strategy. Adding a third source
(e.g. Wallhaven) means one new impl + one enum constant + one `when` branch.

```mermaid
classDiagram
    direction LR

    class WallpaperRepository {
        <<interface>>
        +fetchImages(page, perPage) List~Wallpaper~
        +fetchFeaturedTopics() List~Topic~
        +fetchTopicImages(slug, page, perPage) List~Wallpaper~
    }
    class UnsplashWallpaperRepository {
        -client: UnsplashApiClient
    }
    class PexelsWallpaperRepository {
        -client: PexelsApiClient
    }
    class WallpaperRepositoryProvider {
        +getWallpaperProvider(WallpaperSource) WallpaperRepository
    }
    class WallpaperSource {
        <<enumeration>>
        UNSPLASH
        PEXELS
        +sourceName: String
    }
    class UnsplashApiClient {
        +fetchPhotos() List~UnsplashModel~
        +fetchTopics() List~UnsplashTopic~
        +fetchTopicPhotos() List~UnsplashModel~
    }
    class PexelsApiClient {
        -apiKey: String
        +fetchCurated() PexelsPhotosResponse
        +fetchFeaturedCollections() PexelsCollectionsResponse
        +fetchCollectionPhotos() PexelsCollectionMediaResponse
    }

    WallpaperRepository <|.. UnsplashWallpaperRepository
    WallpaperRepository <|.. PexelsWallpaperRepository
    WallpaperRepositoryProvider o-- UnsplashWallpaperRepository
    WallpaperRepositoryProvider o-- PexelsWallpaperRepository
    WallpaperRepositoryProvider ..> WallpaperSource : selects by
    UnsplashWallpaperRepository --> UnsplashApiClient
    PexelsWallpaperRepository --> PexelsApiClient
    UnsplashApiClient ..> buildWallpaperHttpClient : built from
    PexelsApiClient ..> buildWallpaperHttpClient : built from
```

### 2b. DTO → domain mapping

Each provider has its own wire-format DTOs. Top-level extension mappers
(`data/mapper/`) do **pure translation** into the shared domain types — no policy.
The premium filter is intentionally *not* here; it lives in the Unsplash repository.

```mermaid
classDiagram
    direction LR

    class UnsplashModel {
        +id: String
        +description: String?
        +premium: Boolean?
        +urls: UnsplashURLS
        +user: UnsplashUser
    }
    class PexelsPhoto {
        +id: Int
        +alt: String?
        +photographer: String
        +photographerUrl: String
        +src: PexelsSrc
    }
    class Wallpaper {
        +id: String
        +width: Int
        +height: Int
        +description: String?
        +urls: WallpaperUrls
        +photographerName: String
        +photographerProfileUrl: String
    }
    class WallpaperUrls {
        +thumbUrl: String
        +regularUrl: String
        +fullUrl: String
    }
    class UnsplashTopic {
        +id: String
        +slug: String
        +title: String
    }
    class PexelsCollection {
        +id: String
        +title: String
    }
    class Topic {
        +id: String
        +slug: String
        +title: String
    }

    UnsplashModel *-- UnsplashURLS
    UnsplashModel *-- UnsplashUser
    PexelsPhoto *-- PexelsSrc
    Wallpaper *-- WallpaperUrls

    UnsplashModel ..> Wallpaper : toWallpaper()
    PexelsPhoto ..> Wallpaper : toWallpaper()
    UnsplashTopic ..> Topic : toTopic()
    PexelsCollection ..> Topic : toTopic()
```

> **Mapping notes:** Pexels `id: Int` → `id.toString()`; blank `alt` → `null`
> (`alt?.ifBlank { null }`); `src.medium / large2x / original` → `thumb / regular / full`;
> a Pexels *collection* has no slug, so `toTopic()` uses `slug = id`.

---

## 3. UI state model

`HomeViewModel` exposes a single `StateFlow<WallpaperUIState>`. The nesting makes
illegal states unrepresentable: e.g. you can only be "switching feed" *inside* a
`Success`, never as a free-floating flag. All three are sealed hierarchies.

```mermaid
classDiagram
    class WallpaperUIState {
        <<sealed>>
    }
    class Loading {
        <<object>>
    }
    class Success {
        +selectedFeed: FeedSelection
        +availableTopics: List~Topic~
        +content: ContentState
        +statusMessage: String?
    }
    class Error {
        +errorMessage: String
    }
    WallpaperUIState <|-- Loading
    WallpaperUIState <|-- Success
    WallpaperUIState <|-- Error

    class ContentState {
        <<sealed>>
    }
    class SwitchingFeed {
        <<object>>
    }
    class Loaded {
        +images: List~Wallpaper~
        +isPaginating: Boolean
        +isRefreshing: Boolean
    }
    class FeedFailed {
        +errorMessage: String
    }
    ContentState <|-- SwitchingFeed
    ContentState <|-- Loaded
    ContentState <|-- FeedFailed

    class FeedSelection {
        <<sealed>>
    }
    class All {
        <<object>>
    }
    class FeedTopic["FeedSelection.Topic"] {
        +slug: String
        +displayName: String
    }
    FeedSelection <|-- All
    FeedSelection <|-- FeedTopic

    Success *-- ContentState : content
    Success *-- FeedSelection : selectedFeed
```

> **Naming clash worth knowing:** `FeedSelection.Topic` (a UI selection: slug +
> display name) is distinct from the domain `Topic` (the wire-equivalent featured
> category). `Success` holds both — `selectedFeed` (which chip is active) and
> `availableTopics: List<Topic>` (which chips exist).

---

## 4. UI components & navigation flow

The UI layer is mostly `@Composable` functions (not classes), so it's shown as a
component/flow diagram. `MainActivity` runs a two-stage splash, then a Navigation 3
`NavDisplay` over a `mutableStateList`-backed backstack.

```mermaid
flowchart TD
    MA["MainActivity<br/>(installSplashScreen first)"]
    MA --> CF{"Crossfade(showSplash)"}
    CF -->|true, 500ms| SC["SplashContent<br/>(wordmark + bloom)"]
    CF -->|false| ND["NavDisplay"]

    ND --> BS["backstack:<br/>rememberNavBackStack(HomeScreen)"]
    ND --> EP["entryProvider"]
    EP --> HSD["HomeScreen (NavKey)"]
    EP --> DSD["DetailsScreen(Wallpaper) (NavKey)"]

    HSD --> HS["HomeScreen composable"]
    HS --> TCR["TopicChipRow<br/>(TopicChip atoms)"]
    HS --> SP["SourcePill"]
    SP -->|"opens"| SPS["SourcePickerSheet"]
    SPS -->|"onProviderChanged(source)"| HVM
    HS --> CL1["CenteredLoader"]
    HS -.->|observes uiState + currentWallpaperSource| HVM["HomeViewModel"]
    HVM -.->|scrollToTopEvents| HS
    HS -->|"onClick → backstack.add"| DSD

    DSD --> DS["DetailsScreen composable<br/>(zoom · pan · download · set-as)"]
    DS --> CL2["CenteredLoader"]
    DS --> STB["SpringyTextButton"]
```

> `SplashContent` is deliberately **not** a Nav 3 destination — users never navigate
> to it, so it lives outside `NavDisplay` and is gated by a `Crossfade`, not the backstack.

---

## Keeping this current

These diagrams are hand-maintained, not generated. Touch them when you:
- add/remove a `WallpaperSource` or repository (§2a),
- change a domain type or mapper (§2b),
- restructure `WallpaperUIState` / `ContentState` / `FeedSelection` (§3),
- add a navigation destination (§4).
