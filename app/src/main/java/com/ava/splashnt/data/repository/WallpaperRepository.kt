package com.ava.splashnt.data.repository

import com.ava.splashnt.data.model.UnsplashModel

interface WallpaperRepository {
    suspend fun fetchImages(page: Int, imagesPerPage: Int): List<UnsplashModel>
}