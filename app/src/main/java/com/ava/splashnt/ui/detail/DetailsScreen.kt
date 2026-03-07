package com.ava.splashnt.ui.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.ava.splashnt.data.model.UnsplashModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

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

    var displayHeight by remember { mutableFloatStateOf(0f) }
    var displayWidth by remember { mutableFloatStateOf(0f) }

    var imageWidth by remember { mutableFloatStateOf(0f) }
    var imageHeight by remember { mutableFloatStateOf(0f) }
    var imageAspectRatio: Float

    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val transformCoroutineScope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier
            .background(Color.Black)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val screenHeight = with(LocalDensity.current) { maxHeight.toPx() }
        val screenWidth = with(LocalDensity.current) { maxWidth.toPx() }

        imageAspectRatio = if (imageWidth > 0f) imageHeight / imageWidth else 1f

        displayHeight = screenWidth * imageAspectRatio
        displayWidth = screenWidth

        val transformState =
            rememberTransformableState { centroid, zoomChange, panChange, rotationChange ->

                transformCoroutineScope.launch {
                    val tempScale = scale.value * zoomChange
                    scale.snapTo(tempScale.coerceIn(1f, 5f))

                    val tempOffsetX = offsetX.value + panChange.x * scale.value
                    val maxRangeX = maxOf(0f, (displayWidth * scale.value - screenWidth) / 2)
                    offsetX.snapTo(tempOffsetX.coerceIn(-maxRangeX, maxRangeX))

                    val tempOffsetY = offsetY.value + panChange.y * scale.value
                    val maxRangeY = maxOf(0f, (displayHeight * scale.value - screenHeight) / 2)
                    offsetY.snapTo(tempOffsetY.coerceIn(-maxRangeY, maxRangeY))

                }
            }

        SubcomposeAsyncImage(
            model = fullResImageUrl,
            loading = { ShowLoader() },
            contentDescription = imageDescription,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationX = offsetX.value
                    translationY = offsetY.value
                }
                .transformable(transformState)
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        transformCoroutineScope.launch {
                            if (scale.value == 1f && imageWidth > 0) {
                                scale.animateTo(maxOf(screenHeight / displayHeight, screenWidth / displayWidth))
                            } else {
                                val scaleJob = async { scale.animateTo(1f) }
                                val offsetXJob = async { offsetX.animateTo(0f) }
                                val offsetYJob = async { offsetY.animateTo(0f) }

                                awaitAll(scaleJob, offsetXJob, offsetYJob)
                            }
                        }
                    })
                },
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