package com.ava.splashnt.data.repository

class WallpaperRepositoryProvider(
    private val unsplashRepository: UnsplashWallpaperRepository,
) {
    fun getWallpaperProvider(
        currentWallpaperSource: WallpaperSource,
    ): WallpaperRepository {
        return when(currentWallpaperSource) {
            WallpaperSource.UNSPLASH -> unsplashRepository
        }
    }
}