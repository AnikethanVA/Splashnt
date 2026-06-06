package com.ava.splashnt.data.repository

class WallpaperRepositoryProvider(
    private val unsplashRepository: UnsplashWallpaperRepository,
    private val pexelsWallpaperRepository: PexelsWallpaperRepository
) {
    fun getWallpaperProvider(
        currentWallpaperSource: WallpaperSource,
    ): WallpaperRepository {
        return when(currentWallpaperSource) {
            WallpaperSource.UNSPLASH -> unsplashRepository
            WallpaperSource.PEXELS -> pexelsWallpaperRepository
        }
    }
}