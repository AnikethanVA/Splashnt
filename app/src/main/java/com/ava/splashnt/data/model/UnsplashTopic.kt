package com.ava.splashnt.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UnsplashTopic(
    val id: String,
    val slug: String,
    val title: String,
)