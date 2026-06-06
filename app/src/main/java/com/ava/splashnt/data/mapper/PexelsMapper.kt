package com.ava.splashnt.data.mapper

import com.ava.splashnt.data.model.PexelsCollection
import com.ava.splashnt.data.model.PexelsPhoto
import com.ava.splashnt.data.model.Topic
import com.ava.splashnt.data.model.Wallpaper
import com.ava.splashnt.data.model.WallpaperUrls

fun PexelsPhoto.toWallpaper(): Wallpaper = Wallpaper(
    id = id.toString(),
    width = width,
    height = height,
    description = alt?.ifBlank { null },
    urls = WallpaperUrls(
        thumbUrl = src.medium,
        regularUrl = src.large2x,
        fullUrl = src.original,
    ),
    photographerName = photographer,
    photographerProfileUrl = photographerUrl,
)

fun PexelsCollection.toTopic(): Topic = Topic(
    id = id,
    slug = id,
    title = title,
)