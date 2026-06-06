package com.ava.splashnt.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PexelsCollection(
    val id: String,
    val title: String,
)

@Serializable
data class PexelsCollectionsResponse(
    val collections: List<PexelsCollection>,
)

@Serializable
data class PexelsCollectionMediaResponse(
    val media: List<PexelsPhoto>,
)