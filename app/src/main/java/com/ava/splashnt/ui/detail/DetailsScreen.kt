package com.ava.splashnt.ui.detail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
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
    ShowFullScreenImage(image = image)
}

@Composable
fun ShowFullScreenImage(
    modifier: Modifier = Modifier,
    image: UnsplashModel
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

    var shouldShowImageOverlay by remember { mutableStateOf(false) }

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
            model = image.urls.fullUrl,
            loading = { ShowLoader() },
            contentDescription = image.description,
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
                    detectTapGestures(
                        onTap = {
                            shouldShowImageOverlay = !shouldShowImageOverlay
                        },
                        onDoubleTap = {
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

        AnimatedVisibility(
            visible = shouldShowImageOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ImageDetailsOverlay(image)
        }
    }
}

@Composable
fun ImageDetailsOverlay(image: UnsplashModel) {

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0.7f to Color.Transparent,
                    1.0f to Color.Black.copy(alpha = 0.7f),
                    startY = 0.0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = image.user.userName
        )
        Text(
            modifier = Modifier
                .padding(start = 16.dp)
                .clickable {
                    openLinkInBrowser(image.user.userLinks.photographerProfileUrl, context)
                }
            ,
            text = image.user.userLinks.photographerProfileUrl,
            style = TextStyle(
                textDecoration = TextDecoration.Underline
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun openLinkInBrowser(link: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, link.toUri())
    try {
        context.startActivity(intent)
    }catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No application found to open the link", Toast.LENGTH_SHORT).show()
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