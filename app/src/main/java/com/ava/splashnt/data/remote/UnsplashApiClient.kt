package com.ava.splashnt.data.remote

import com.ava.splashnt.data.model.UnsplashModel
import com.ava.splashnt.data.model.UnsplashTopic
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class UnsplashApiClient {
    val client = buildWallpaperHttpClient {
        defaultRequest {
            url("https://unsplash.com/napi/")
        }
    }

    suspend fun fetchPhotos(page: Int, imagesPerPage: Int): List<UnsplashModel> {
        return client.get("photos") {
            parameter("page", page)
            parameter("per_page", imagesPerPage)
        }.body<List<UnsplashModel>>()
    }

    suspend fun fetchTopics(): List<UnsplashTopic> {
        return client.get("topics") {
            parameter("featured", true)
            parameter("per_page", 30)
        }.body<List<UnsplashTopic>>()
    }

    suspend fun fetchTopicPhotos(slug: String, page: Int, imagesPerPage: Int): List<UnsplashModel> {
        return client.get("topics/$slug/photos") {
            parameter("page", page)
            parameter("per_page", imagesPerPage)
        }.body<List<UnsplashModel>>()
    }
}