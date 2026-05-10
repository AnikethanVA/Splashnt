package com.ava.splashnt.data.repository

import com.ava.splashnt.data.model.Topic
import com.ava.splashnt.data.model.Wallpaper

interface WallpaperRepository {
    suspend fun fetchImages(page: Int, imagesPerPage: Int): List<Wallpaper>
    suspend fun fetchFeaturedTopics(): List<Topic>
    suspend fun fetchTopicImages(slug: String, page: Int, imagesPerPage: Int): List<Wallpaper>
}