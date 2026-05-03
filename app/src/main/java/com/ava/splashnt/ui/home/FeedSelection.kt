package com.ava.splashnt.ui.home

sealed class FeedSelection {
    object All: FeedSelection()
    data class Topic(
        val slug: String,
        val displayName: String
    ): FeedSelection()
}