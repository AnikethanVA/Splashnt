package com.ava.splashnt.ui

import android.media.Image
import com.ava.splashnt.data.model.UnsplashModel

sealed class WallpaperUiState {
    object Loading: WallpaperUiState()
    data class Success(
        val images: List<UnsplashModel>,
        val isPaginating: Boolean = false
    ): WallpaperUiState()
    data class Error(val errorMessage: String): WallpaperUiState()
}