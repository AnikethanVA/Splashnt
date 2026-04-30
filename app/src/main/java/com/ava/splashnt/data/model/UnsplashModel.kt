package com.ava.splashnt.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashModel(

    val id: String,

    /*
    Can the below width and height be used
    instead of waiting for it in the onSuccess of coil SubcomposeAsyncImage in DetailsScreen ?
    */
    val width: Int,

    val height: Int,

    val description: String?,

    val urls: UnsplashURLS,

    val user: UnsplashUser,

    val premium: Boolean? = null

)

@Serializable
data class UnsplashURLS(

    @SerialName("full")
    val fullUrl: String,

    @SerialName("regular")
    val regularUrl: String,

    @SerialName("thumb")
    val thumbUrl: String

)

@Serializable
data class UnsplashUser(

    @SerialName("name")
    val userName: String,

    @SerialName("links")
    val userLinks: UnsplashLinks
)

@Serializable
data class UnsplashLinks(

    @SerialName("html")
    val photographerProfileUrl: String
)