package com.ava.splashnt.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashModel(

    val id: String,

    val description: String?,

    val urls: UnsplashURLS,

    val user: UnsplashUser

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