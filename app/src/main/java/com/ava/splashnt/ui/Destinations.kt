package com.ava.splashnt.ui

import androidx.navigation3.runtime.NavKey
import com.ava.splashnt.data.model.UnsplashModel
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen: NavKey

@Serializable
data class DetailsScreen(
    val image: UnsplashModel
): NavKey