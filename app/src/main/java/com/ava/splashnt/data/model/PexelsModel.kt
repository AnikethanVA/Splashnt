package com.ava.splashnt.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PexelsPhoto(
    val id: Int,
    val width: Int,
    val height: Int,
    val photographer: String,

    @SerialName("photographer_url")
    val photographerUrl: String,

    val alt: String? = null,
    val src: PexelsSrc,
)

@Serializable
data class PexelsSrc(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String,
)

@Serializable
data class PexelsPhotosResponse(
    val photos: List<PexelsPhoto>,
)