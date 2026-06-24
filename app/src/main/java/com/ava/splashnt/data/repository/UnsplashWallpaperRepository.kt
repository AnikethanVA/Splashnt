package com.ava.splashnt.data.repository

import com.ava.splashnt.data.mapper.toTopic
import com.ava.splashnt.data.mapper.toWallpaper
import com.ava.splashnt.data.model.Topic
import com.ava.splashnt.data.model.Wallpaper
import com.ava.splashnt.data.remote.UnsplashApiClient

class UnsplashWallpaperRepository(private val client: UnsplashApiClient): WallpaperRepository {
    override suspend fun fetchImages(page: Int, imagesPerPage: Int): List<Wallpaper> {
        return client
            .fetchPhotos(page, imagesPerPage)
            .filter { it.premium != true }
            .map { it.toWallpaper() }
    }

    override suspend fun fetchFeaturedTopics(): List<Topic> {
        return client
            .fetchTopics()
            .filter { it.totalPhotos > 0 }
            .map { it.toTopic() }
    }

    override suspend fun fetchTopicImages(
        slug: String,
        page: Int,
        imagesPerPage: Int
    ): List<Wallpaper> {
        return client
            .fetchTopicPhotos(slug, page, imagesPerPage)
            .filter { it.premium != true }
            .map { it.toWallpaper() }
    }
}