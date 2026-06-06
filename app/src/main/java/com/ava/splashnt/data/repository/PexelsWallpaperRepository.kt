package com.ava.splashnt.data.repository

import com.ava.splashnt.data.mapper.toTopic
import com.ava.splashnt.data.mapper.toWallpaper
import com.ava.splashnt.data.model.Topic
import com.ava.splashnt.data.model.Wallpaper
import com.ava.splashnt.data.remote.PexelsApiClient

class PexelsWallpaperRepository(private val client: PexelsApiClient): WallpaperRepository {
    override suspend fun fetchImages(
        page: Int,
        imagesPerPage: Int
    ): List<Wallpaper> {
        return client.fetchCurated(page, imagesPerPage).photos.map { it.toWallpaper() }
    }

    override suspend fun fetchFeaturedTopics(): List<Topic> {
        return client.fetchFeaturedCollections(perPage = 30).collections.map { it.toTopic() }
    }

    override suspend fun fetchTopicImages(
        slug: String,
        page: Int,
        imagesPerPage: Int
    ): List<Wallpaper> {
        return client.fetchCollectionPhotos(slug, page, imagesPerPage).media.map { it.toWallpaper() }
    }
}