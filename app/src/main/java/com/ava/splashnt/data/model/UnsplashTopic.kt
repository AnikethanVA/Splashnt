package com.ava.splashnt.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashTopic(
    val id: String,
    val slug: String,
    val title: String,

    @SerialName("total_photos")
    val totalPhotos: Int = 0,
)