package com.ava.splashnt.data.repository

import com.ava.splashnt.data.model.UnsplashModel
import com.ava.splashnt.data.remote.UnsplashApiClient

class UnsplashWallpaperRepository(private val client: UnsplashApiClient): WallpaperRepository {
    override suspend fun fetchImages(page: Int, imagesPerPage: Int): List<UnsplashModel> {
        return client.fetchPhotos(page, imagesPerPage)
    }
}