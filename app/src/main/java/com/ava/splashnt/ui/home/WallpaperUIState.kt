package com.ava.splashnt.ui.home

import com.ava.splashnt.data.model.UnsplashTopic

sealed class WallpaperUIState {
    object Loading: WallpaperUIState()
    data class Success(
        val selectedFeed: FeedSelection,
        val availableTopics: List<UnsplashTopic>,
        val content: ContentState,
        val statusMessage: String? = null
    ): WallpaperUIState()
    data class Error(val errorMessage: String): WallpaperUIState()
}