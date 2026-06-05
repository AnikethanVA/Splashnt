package com.ava.splashnt.data.remote

import com.ava.splashnt.data.model.PexelsCollectionMediaResponse
import com.ava.splashnt.data.model.PexelsCollectionsResponse
import com.ava.splashnt.data.model.PexelsPhotosResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders

class PexelsApiClient(
    apiKey: String
) {
    val client = buildWallpaperHttpClient {
        defaultRequest {
            url("https://api.pexels.com/v1/")
            header(HttpHeaders.Authorization, apiKey)
        }
    }

    suspend fun fetchCurated(page: Int, imagesPerPage: Int): PexelsPhotosResponse {
        return client.get("curated") {
            parameter("page", page)
            parameter("per_page", imagesPerPage)
        }.body()
    }

    suspend fun fetchFeaturedCollections(perPage: Int): PexelsCollectionsResponse {
        return client.get("collections/featured") {
            parameter("per_page", perPage)
        }.body()
    }

    suspend fun fetchCollectionPhotos(id: String, page: Int, imagesPerPage: Int): PexelsCollectionMediaResponse {
        return client.get("collections/$id") {
            parameter("type", "photos")
            parameter("page", page)
            parameter("per_page", imagesPerPage)
        }.body()
    }
}