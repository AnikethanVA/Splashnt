package com.ava.splashnt.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

fun buildWallpaperHttpClient(
    configure: HttpClientConfig<*>.() -> Unit = {}
): HttpClient = HttpClient(Android) {
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

    configure()
}