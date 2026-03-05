package com.ava.splashnt.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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

    var displayHeight: Float
    var displayWidth: Float

    var imageWidth by remember { mutableFloatStateOf(0f) }
    var imageHeight by remember { mutableFloatStateOf(0f) }
    var imageAspectRatio: Float

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints (
        modifier = modifier
            .background(Color.Black)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val boxScope = this

        val screenHeight = with(LocalDensity.current){ boxScope.maxHeight.toPx() }
        val screenWidth = with(LocalDensity.current){ boxScope.maxWidth.toPx() }

        imageAspectRatio = if (imageWidth > 0f) imageHeight / imageWidth else 1f

        val transformState = rememberTransformableState { centroid, zoomChange, panChange, rotationChange ->

            scale *= zoomChange
            scale = scale.coerceIn(1f, 5f)

            displayHeight = screenWidth * imageAspectRatio
            displayWidth = screenWidth

            offsetX += panChange.x * scale
            val maxRangeX = maxOf(0f,(displayWidth*scale - screenWidth)/2)
            offsetX = offsetX.coerceIn(-maxRangeX, maxRangeX)

            offsetY += panChange.y * scale
            val maxRangeY = maxOf(0f,(displayHeight*scale - screenHeight)/2)
            offsetY = offsetY.coerceIn(-maxRangeY, maxRangeY)
        }

        SubcomposeAsyncImage (
            model = fullResImageUrl,
            loading = { ShowLoader() },
            contentDescription = imageDescription,
            modifier = modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }.transformable(transformState),
            contentScale = ContentScale.FillWidth,
            onSuccess = { image ->
                val drawable = image.result.drawable
                imageWidth = drawable.intrinsicWidth.toFloat()
                imageHeight = drawable.intrinsicHeight.toFloat()
            },
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