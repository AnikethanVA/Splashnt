package com.ava.splashnt.ui

import androidx.navigation3.runtime.NavKey
import com.ava.splashnt.data.model.UnsplashModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destinations: NavKey
@Serializable
object HomeScreen: Destinations

@Serializable
data class DetailsScreen(
    val image: UnsplashModel
): Destinations