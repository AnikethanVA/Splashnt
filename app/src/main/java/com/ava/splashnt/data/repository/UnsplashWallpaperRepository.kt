package com.ava.splashnt.data.repository

import com.ava.splashnt.data.model.UnsplashModel
import com.ava.splashnt.data.model.UnsplashTopic
import com.ava.splashnt.data.remote.UnsplashApiClient

class UnsplashWallpaperRepository(private val client: UnsplashApiClient): WallpaperRepository {
    override suspend fun fetchImages(page: Int, imagesPerPage: Int): List<UnsplashModel> {
        return client.fetchPhotos(page, imagesPerPage).filter { it.premium != true }
    }

    override suspend fun fetchFeaturedTopics(): List<UnsplashTopic> {
        return client.fetchTopics()
    }

    override suspend fun fetchTopicImages(
        slug: String,
        page: Int,
        imagesPerPage: Int
    ): List<UnsplashModel> {
        return client.fetchTopicPhotos(slug, page, imagesPerPage).filter { it.premium != true }
    }
}