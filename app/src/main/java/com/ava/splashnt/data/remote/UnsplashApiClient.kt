package com.ava.splashnt.data.remote

import com.ava.splashnt.data.model.UnsplashModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
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
            logger = Logger.DEFAULT
            level = LogLevel.INFO
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
}