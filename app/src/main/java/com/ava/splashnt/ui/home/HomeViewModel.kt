package com.ava.splashnt.ui.home

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ava.splashnt.data.model.Topic
import com.ava.splashnt.data.model.Wallpaper
import com.ava.splashnt.data.repository.WallpaperRepository
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
    private var currentFetchWallpaperJob: Job? = null
    private var currentFetchTopicsJob: Job? = null

    private var pendingTopics: List<Topic>? = null
    private var topicsLoadFailed = false
    val uiState = _uiState.asStateFlow()

    init {
        fetchWallpapers(isPaginating = false, isRefreshing = false)
        fetchTopics()
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

    fun onChipClicked(newFeed: FeedSelection) {
        val currentSuccessState = _uiState.value as? Success ?: return
        val isSameSelection = currentSuccessState.selectedFeed == newFeed
        val hasFeedFetchFailed = currentSuccessState.content is ContentState.FeedFailed

        if(isSameSelection && !hasFeedFetchFailed) return

        currentPage = 1
        currentFetchWallpaperJob?.cancel()
        _uiState.value = currentSuccessState.copy(
            selectedFeed = newFeed,
            content = ContentState.SwitchingFeed
        )

        currentFetchWallpaperJob = viewModelScope.launch {
            val currentRepo = wallpaperRepositoryProvider.getWallpaperProvider(currentWallpaperSource)
            try {
                val fetchedImages = fetchImagesForFeed(
                    selectedFeed = newFeed,
                    currentRepo = currentRepo,
                    pageToFetch = currentPage,
                    imagesPerPage = defaultImagesPerPage
                )
                val currentSuccessState = _uiState.value as? Success ?: return@launch
                _uiState.value = currentSuccessState.copy(content = ContentState.Loaded(fetchedImages))
            } catch(_: Exception) {
                val current = _uiState.value as? Success ?: return@launch
                val errorMessage = when (newFeed) {
                    FeedSelection.All -> "Couldn't load wallpapers"
                    is FeedSelection.Topic -> "Couldn't load \"${newFeed.displayName}\""
                }
                _uiState.value = current.copy(content = ContentState.FeedFailed(errorMessage))
            }
        }
    }

    private suspend fun fetchImagesForFeed(
        selectedFeed: FeedSelection,
        currentRepo: WallpaperRepository,
        pageToFetch: Int,
        imagesPerPage: Int
    ): List<Wallpaper> {
        return when(selectedFeed) {
            is FeedSelection.All -> {
                currentRepo.fetchImages(page = pageToFetch, imagesPerPage = imagesPerPage)
            }
            is FeedSelection.Topic -> {
                currentRepo.fetchTopicImages(slug = selectedFeed.slug, page = pageToFetch, imagesPerPage = imagesPerPage)
            }
        }
    }

    private fun fetchTopics() {
        currentFetchTopicsJob?.cancel()
        currentFetchTopicsJob = viewModelScope.launch {
            try {
                val fetchedTopics = wallpaperRepositoryProvider.getWallpaperProvider(currentWallpaperSource).fetchFeaturedTopics()
                val currentState = _uiState.value
                if(currentState is Success) {
                    _uiState.value = currentState.copy(availableTopics = fetchedTopics)
                } else {
                    pendingTopics = fetchedTopics
                }
            } catch(_: Exception) {
                val current = _uiState.value
                if (current is Success) {
                    _uiState.value = current.copy(statusMessage = "Couldn't load topics")
                } else {
                    pendingTopics = emptyList()
                    topicsLoadFailed = true
                }
            }
        }
    }

    private fun fetchWallpapers(isPaginating: Boolean, isRefreshing: Boolean) {
        val currentSuccess = _uiState.value as? Success
        val selectedFeed = currentSuccess?.selectedFeed ?: FeedSelection.All
        val currentImages = (currentSuccess?.content as? ContentState.Loaded)?.images ?: emptyList()

        currentFetchWallpaperJob?.cancel()
        currentFetchWallpaperJob = viewModelScope.launch {
            val currentRepo = wallpaperRepositoryProvider.getWallpaperProvider(currentWallpaperSource)
            try {
                if((isPaginating || isRefreshing) && currentImages.isNotEmpty()) {
                    _uiState.value = Success(
                        selectedFeed = selectedFeed,
                        availableTopics = currentSuccess?.availableTopics ?: emptyList(),
                        content = ContentState.Loaded(
                            images = currentImages,
                            isPaginating = isPaginating,
                            isRefreshing = isRefreshing,
                        )
                    )
                }

                val nextPageImages = fetchImagesForFeed(
                    selectedFeed = selectedFeed,
                    currentRepo = currentRepo,
                    pageToFetch = currentPage,
                    imagesPerPage = defaultImagesPerPage
                )
                val allImages = if(isRefreshing) {
                    nextPageImages
                } else {
                    currentImages + nextPageImages
                }

                val topicsToShow = currentSuccess?.availableTopics ?: pendingTopics ?: emptyList()
                pendingTopics = null

                _uiState.value = Success(
                    selectedFeed = selectedFeed,
                    availableTopics = topicsToShow,
                    content = ContentState.Loaded(images = allImages),
                    statusMessage = if(topicsLoadFailed) {
                        topicsLoadFailed = false
                        "Couldn't load topics"
                    } else {
                        null
                    }
                )
            } catch(exception: Exception) {
                if((isPaginating || isRefreshing) && currentImages.isNotEmpty()) {
                    _uiState.value = Success(
                        selectedFeed = selectedFeed,
                        availableTopics = currentSuccess?.availableTopics ?: emptyList(),
                        content = ContentState.Loaded(images = currentImages),
                        statusMessage = "Something Went Wrong")
                } else {
                    _uiState.value = Error("Error Message: ${exception.message}")
                }
            }
        }
    }
}