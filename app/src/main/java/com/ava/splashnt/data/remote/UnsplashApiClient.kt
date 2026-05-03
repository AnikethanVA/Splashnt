package com.ava.splashnt.data.remote

import com.ava.splashnt.data.model.UnsplashModel
import com.ava.splashnt.data.model.UnsplashTopic
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

class UnsplashApiClient {
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }

        install(HttpRequestRetry) {
            maxRetries = 3
            exponentialDelay()
            retryOnExceptionIf { _, throwable ->  throwable is IOException }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 20000
            socketTimeoutMillis = 20000
            connectTimeoutMillis = 10000
        }

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