package com.ava.splashnt.ui.home

import com.ava.splashnt.data.model.UnsplashModel

sealed class WallpaperUIState {
    object Loading: WallpaperUIState()
    data class Success(
        val images: List<UnsplashModel>,
        val isPaginating: Boolean = false,
        val isRefreshing: Boolean = false,
        val statusMessage: String? = null
    ): WallpaperUIState()
    data class Error(val errorMessage: String): WallpaperUIState()
}