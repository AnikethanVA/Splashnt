package com.ava.splashnt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ava.splashnt.data.model.UnsplashModel
import com.ava.splashnt.data.repository.WallpaperRepository
import com.ava.splashnt.data.repository.WallpaperRepositoryProvider
import com.ava.splashnt.data.repository.WallpaperSource
import com.ava.splashnt.ui.home.WallpaperUIState.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val wallpaperRepositoryProvider: WallpaperRepositoryProvider
): ViewModel() {

    private var currentWallpaperSource = WallpaperSource.UNSPLASH

    private var currentPage = 1
    private var defaultImagesPerPage = 10
    private var _uiState = MutableStateFlow<WallpaperUIState>(Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchWallpapers(isPaginating = false)
    }

    fun onProviderChanged(newWallpaperSource: WallpaperSource) {
        if(currentWallpaperSource != newWallpaperSource) {
            _uiState.value = Loading
            currentWallpaperSource = newWallpaperSource
            currentPage = 1
            fetchWallpapers(isPaginating = false)
        }
    }

    fun loadMoreImages() {
        currentPage ++
        fetchWallpapers(isPaginating = true)
    }

    private fun fetchWallpapers(isPaginating: Boolean) {
        viewModelScope.launch {
            try {

                val currentImages = if(_uiState.value is Success) (_uiState.value as Success).images else emptyList()

                if(currentImages.isNotEmpty()) {
                    _uiState.value = Success(currentImages, isPaginating)
                }

                val nextPageImages = wallpaperRepositoryProvider.getWallpaperProvider(currentWallpaperSource).fetchImages(currentPage, defaultImagesPerPage)

                val allImages = currentImages + nextPageImages

                _uiState.value = Success( allImages, false)
            } catch(exception: Exception) {
                _uiState.value = Error("Error Message: ${exception.message}")
            }
        }
    }
}