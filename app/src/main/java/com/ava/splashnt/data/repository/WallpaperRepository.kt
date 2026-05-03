package com.ava.splashnt.data.repository

import com.ava.splashnt.data.model.UnsplashModel
import com.ava.splashnt.data.model.UnsplashTopic

interface WallpaperRepository {
    suspend fun fetchImages(page: Int, imagesPerPage: Int): List<UnsplashModel>
    suspend fun fetchFeaturedTopics(): List<UnsplashTopic>
    suspend fun fetchTopicImages(slug: String, page: Int, imagesPerPage: Int): List<UnsplashModel>
}