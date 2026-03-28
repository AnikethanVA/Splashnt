package com.ava.splashnt.ui.detail

import android.Manifest
import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.toBitmap
import com.ava.splashnt.data.model.UnsplashModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    image: UnsplashModel
) {
    ShowFullScreenImage(imageModel = image)
}

@Composable
fun ShowFullScreenImage(
    modifier: Modifier = Modifier,
    imageModel: UnsplashModel
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

    var retryKey by remember { mutableIntStateOf(0) }

    var imageBitmap: Bitmap? by remember { mutableStateOf(null) }

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
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageModel.urls.fullUrl)
                .memoryCacheKey("${imageModel.urls.fullUrl}_$retryKey")
                .build(),
            loading = { ShowLoader() },
            contentDescription = imageModel.description,
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
                                    scale.animateTo(
                                        maxOf(
                                            screenHeight / displayHeight,
                                            screenWidth / displayWidth
                                        )
                                    )
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
                val resultImageBitmap = image.result.image.toBitmap()
                imageWidth = resultImageBitmap.width.toFloat()
                imageHeight = resultImageBitmap.height.toFloat()
                imageBitmap = resultImageBitmap
            },
            error = {
                ShowLoadImageError(onRetryClicked = {
                    retryKey ++
                })
            },
        )

        AnimatedVisibility(
            visible = shouldShowImageOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ImageDetailsOverlay(imageModel, imageBitmap, this)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailsOverlay(
    imageModel: UnsplashModel,
    imageBitmap: Bitmap?,
    animatedScope: AnimatedVisibilityScope
) {

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        onDownloadImageClicked(context, imageModel)
    }

    var shouldShowSetAsAlertDialog by remember { mutableStateOf(false) }

    val setWallpaperScope = rememberCoroutineScope { Dispatchers.IO }

    ShowSetAsAlertDialog(
        shouldShowSetAsAlertDialog,
        onDismissCallback = { shouldShowSetAsAlertDialog = false },
        onWallpaperLocationSelected = { wallpaperLocation ->
            imageBitmap?.let { imageBitmap ->
                setWallpaperScope.launch {
                    setWallpaperAs(context, imageBitmap, wallpaperLocation.value)
                }
                shouldShowSetAsAlertDialog = false
            }
        }
    )

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                shape = CircleShape,
                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                onClick = {
                    if((context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                    else {
                        onDownloadImageClicked(context, imageModel)
                    }
                },
                content = {
                    Text(text = "Download", color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Outlined.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.onPrimary)
                },
            )

            imageBitmap?.let {
                TextButton(
                    shape = CircleShape,
                    colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                    onClick = {
                        shouldShowSetAsAlertDialog = true
                    },
                    content = {
                        Text(text = "Set As", color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Outlined.Wallpaper, contentDescription = "Set As", tint = MaterialTheme.colorScheme.onPrimary)
                    },
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
            ,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = imageModel.user.userName,
                    color = Color.White
                )
                Text(
                    modifier = Modifier
                        .clickable {
                            openLinkInBrowser(imageModel.user.userLinks.photographerProfileUrl, context)
                        }
                    ,
                    text = imageModel.user.userLinks.photographerProfileUrl,
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowSetAsAlertDialog(
    shouldShow: Boolean,
    onDismissCallback: () -> Unit,
    onWallpaperLocationSelected: (WallpaperLocation) -> Unit
) {
    if(shouldShow) {
        BasicAlertDialog(
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = true
            ),
            onDismissRequest = onDismissCallback,
        ) {
            SetAsDialog(onWallpaperLocationSelected)
        }
    }
}

@Composable
private fun SetAsDialog(
    onWallpaperLocationSelected: (WallpaperLocation) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Where do you want to set this wallpaper ?",
                fontSize = 16.sp,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = {
                        onWallpaperLocationSelected(WallpaperLocation.HOMESCREEN)
                    }
                ) {
                    Text("Home Screen")
                }

                TextButton(
                    onClick = {
                        onWallpaperLocationSelected(WallpaperLocation.LOCKSCREEN)
                    }
                ) {
                    Text("Lock Screen")
                }

                TextButton(
                    onClick = {
                        onWallpaperLocationSelected(WallpaperLocation.BOTH)
                    }
                ) {
                    Text("Both")
                }
            }
        }
    }

}

private fun setWallpaperAs(context: Context, image: Bitmap, userChoice: Int) {
    WallpaperManager
        .getInstance(context)
        .setBitmap(
            image,
            null,
            true,
            userChoice
        )
}

private enum class WallpaperLocation(val value: Int) {
    HOMESCREEN(1),
    LOCKSCREEN(2),
    BOTH(3)
}

private fun onDownloadImageClicked(context: Context, image: UnsplashModel) {
    val downloadManager: DownloadManager = context.getSystemService(DownloadManager::class.java)
    val downloadRequest = DownloadManager
        .Request(image.urls.fullUrl.toUri())
        .setTitle("Downloading Wallpaper - ${image.id}")
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${image.id}.jpg")
        .setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    val status = downloadManager.enqueue(downloadRequest)
    if(status == -1L) {
        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
    } else if(context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context, "Download Started. Check Notification", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Notification Permission has been denied. Check Downloads folder for the wallpaper", Toast.LENGTH_SHORT).show()
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

@Composable
fun ShowLoadImageError(onRetryClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .size(150.dp),
            imageVector = Icons.Rounded.Warning,
            contentDescription = "Image Load Error",
            tint = Color.White
        )
        Text(
            text = "Failed to load the image",
            style = TextStyle(
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            shape = CircleShape,
            colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = onRetryClicked,
            content = {
                Text(text = "Retry", color = MaterialTheme.colorScheme.onPrimary)
                Icon(imageVector = Icons.Outlined.Replay, contentDescription = "Retry")
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