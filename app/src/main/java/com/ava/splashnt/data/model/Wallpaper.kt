package com.ava.splashnt.data.model


import kotlinx.serialization.Serializable

@Serializable
data class Wallpaper(
    val id: String,
    val width: Int,
    val height: Int,
    val description: String?,
    val urls: WallpaperUrls,
    val photographerName: String,
    val photographerProfileUrl: String,
)

@Serializable
data class WallpaperUrls(
    val thumbUrl: String,
    val regularUrl: String,
    val fullUrl: String,
)