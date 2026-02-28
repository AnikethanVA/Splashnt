package com.ava.splashnt.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.ava.splashnt.data.model.UnsplashModel

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    image: UnsplashModel
) {
    ShowFullScreenImage(fullResImageUrl = image.urls.fullUrl, imageDescription = image.description)
}

@Composable
fun ShowFullScreenImage(
    modifier: Modifier = Modifier,
    fullResImageUrl: String,
    imageDescription: String?
) {
    Box(
        modifier = modifier
            .background(Color.Black)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage (
            model = fullResImageUrl,
            loading = { ShowLoader() },
            contentDescription = imageDescription,
            modifier = modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShowLoader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black)
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ContainedLoadingIndicator()
    }
}