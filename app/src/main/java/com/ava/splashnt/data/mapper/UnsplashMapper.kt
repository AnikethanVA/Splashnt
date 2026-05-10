package com.ava.splashnt.data.mapper

import com.ava.splashnt.data.model.Topic
import com.ava.splashnt.data.model.UnsplashModel
import com.ava.splashnt.data.model.UnsplashTopic
import com.ava.splashnt.data.model.Wallpaper
import com.ava.splashnt.data.model.WallpaperUrls

fun UnsplashModel.toWallpaper(): Wallpaper = Wallpaper(
    id = id,
    width = width,
    height = height,
    description = description,
    urls = WallpaperUrls(
        thumbUrl = urls.thumbUrl,
        regularUrl = urls.regularUrl,
        fullUrl = urls.fullUrl
    ),
    photographerName = user.userName,
    photographerProfileUrl = user.userLinks.photographerProfileUrl
)

fun UnsplashTopic.toTopic(): Topic = Topic(
    id = id,
    slug = slug,
    title = title
)