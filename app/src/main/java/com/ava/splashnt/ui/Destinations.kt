package com.ava.splashnt.ui

import com.ava.splashnt.data.model.UnsplashModel
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

@Serializable
data class DetailsScreen(
    val image: UnsplashModel
)