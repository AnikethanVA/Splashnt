package com.ava.splashnt.ui.home

import com.ava.splashnt.data.model.Wallpaper

sealed class ContentState {
    object SwitchingFeed: ContentState()
    data class Loaded(
        val images: List<Wallpaper>,
        val isPaginating: Boolean = false,
        val isRefreshing: Boolean = false,
    ): ContentState()
    data class FeedFailed(val errorMessage: String): ContentState()
}