package com.ava.splashnt.ui.home

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ava.splashnt.data.repository.WallpaperRepositoryProvider
import com.ava.splashnt.data.repository.WallpaperSource
import com.ava.splashnt.ui.home.WallpaperUIState.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val wallpaperRepositoryProvider: WallpaperRepositoryProvider
): ViewModel() {

    private var currentWallpaperSource = WallpaperSource.UNSPLASH

    private var currentPage = 1
    private var defaultImagesPerPage = 20
    private val _uiState = MutableStateFlow<WallpaperUIState>(Loading)

    // Storing lazyStaggeredGridState here to fix images getting shuffled when coming back from DisplayDetails screen to HomeScreen.
    val lazyStaggeredGridState: LazyStaggeredGridState = LazyStaggeredGridState(0,0)
    private var currentFetchJob: Job? = null
    val uiState = _uiState.asStateFlow()

    init {
        fetchWallpapers(isPaginating = false, isRefreshing = false)
    }

    fun onProviderChanged(newWallpaperSource: WallpaperSource) {
        if(currentWallpaperSource != newWallpaperSource) {
            _uiState.value = Loading
            currentWallpaperSource = newWallpaperSource
            currentPage = 1
            fetchWallpapers(isPaginating = false, isRefreshing = false)
        }
    }

    fun loadMoreImages() {
        currentPage ++
        fetchWallpapers(isPaginating = true, isRefreshing = false)
    }

    fun onRefresh() {
        currentPage = 1
        fetchWallpapers(isPaginating = false, isRefreshing = true)
    }

    fun onStatusMessageShown() {
        val currentState = _uiState.value
        if(currentState is Success && currentState.statusMessage != null) {
            _uiState.value = currentState.copy(statusMessage = null)
        }
    }

    private fun fetchWallpapers(isPaginating: Boolean, isRefreshing: Boolean) {
        val currentImages = if(_uiState.value is Success) (_uiState.value as Success).images else emptyList()

        currentFetchJob?.cancel()
        currentFetchJob = viewModelScope.launch {
            try {
                if((isPaginating || isRefreshing) && currentImages.isNotEmpty()) {
                    _uiState.value = Success(currentImages, isPaginating = isPaginating, isRefreshing = isRefreshing)
                }

                val nextPageImages = wallpaperRepositoryProvider.getWallpaperProvider(currentWallpaperSource).fetchImages(currentPage, defaultImagesPerPage)
                val allImages = if(isRefreshing) {
                    nextPageImages
                } else {
                    currentImages + nextPageImages
                }
                _uiState.value = Success(allImages, isPaginating = false, isRefreshing = false)
            } catch(exception: Exception) {
                if((isPaginating || isRefreshing) && currentImages.isNotEmpty()) {
                    _uiState.value = Success(currentImages, isPaginating = false, isRefreshing = false, statusMessage = "Something Went Wrong")
                } else {
                    _uiState.value = Error("Error Message: ${exception.message}")
                }
            }
        }
    }
}