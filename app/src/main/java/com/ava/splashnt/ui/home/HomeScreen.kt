package com.ava.splashnt.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.Companion.FullLine
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ava.splashnt.R
import com.ava.splashnt.data.model.Wallpaper
import com.ava.splashnt.ui.common.CenteredLoader
import com.ava.splashnt.ui.common.SpringyTextButton
import com.ava.splashnt.ui.home.WallpaperUIState.Error
import com.ava.splashnt.ui.home.WallpaperUIState.Loading
import com.ava.splashnt.ui.home.WallpaperUIState.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onImageClicked: (Wallpaper) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSource by viewModel.currentWallpaperSource.collectAsState()
    var shouldShowSourceSelector by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier,
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = buildAnnotatedString{

                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    ) {
                        append(stringResource(R.string.app_name))
                    }

                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 60.sp
                        )
                    ) {
                        append(".")
                    }
                },
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Default,
                    letterSpacing = (-1.5).sp,
                    lineHeight = 44.sp,
                )
            )

            SourcePill(
                currentSource = currentSource,
                onClick = { shouldShowSourceSelector = true }
            )
        }

        when(val state = uiState) {
            is Loading -> {
                CenteredLoader()
            }
            is Error -> {
                ShowError(errorMessage = state.errorMessage)
            }

            is Success -> {
                ShowWallpapers(
                    wallpaperUIState = state,
                    lazyStaggeredGridStateFromViewModel = viewModel.lazyStaggeredGridState,
                    onLoadMore = viewModel::loadMoreImages,
                    onRefresh = viewModel::onRefresh,
                    onChipClicked = viewModel::onChipClicked,
                    scrollToTopEvents = viewModel.scrollToTopEvents,
                    onStatusMessageShown = viewModel::onStatusMessageShown,
                    onImageClicked = onImageClicked,
                )
            }
        }

        if(shouldShowSourceSelector) {
            SourcePickerSheet(
                currentSource = currentSource,
                onClickSource = viewModel::onProviderChanged,
                onDismiss = { shouldShowSourceSelector = false }
            )
        }
    }
}

@Composable
fun ShowError(
    modifier: Modifier = Modifier,
    errorMessage: String
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "An error occurred: $errorMessage",
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ShowWallpapers(
    modifier: Modifier = Modifier,
    wallpaperUIState: Success,
    lazyStaggeredGridStateFromViewModel: LazyStaggeredGridState,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onChipClicked: (FeedSelection) -> Unit,
    scrollToTopEvents: Flow<Unit>,
    onStatusMessageShown: () -> Unit,
    onImageClicked: (Wallpaper) -> Unit
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val content = wallpaperUIState.content
    val isRefreshing = (content as? ContentState.Loaded)?.isRefreshing == true
    val isPaginating = (content as? ContentState.Loaded)?.isPaginating == true


    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopicChipRow(
                selectedFeed = wallpaperUIState.selectedFeed,
                availableTopics = wallpaperUIState.availableTopics,
                onChipClicked = onChipClicked
            )

            PullToRefreshBox(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                isRefreshing = isRefreshing,
                onRefresh = onRefresh
            ) {
                when(content) {
                    is ContentState.Loaded -> {
                        LazyVerticalStaggeredGrid(
                            state = lazyStaggeredGridStateFromViewModel,
                            columns = StaggeredGridCells.Fixed(2),
                            verticalItemSpacing = 8.dp,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items = content.images, key = { it.id }) { image ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(image.width.toFloat() / image.height),
                                    onClick = {
                                        onImageClicked(image)
                                    },
                                    shape = RoundedCornerShape(percent = 20),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 8.dp,
                                        pressedElevation = 0.dp,
                                    )
                                ) {
                                    AsyncImage(
                                        model = image.urls.thumbUrl,
                                        contentDescription = image.description,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentScale = ContentScale.FillWidth,
                                    )
                                }
                            }

                            if (content.isPaginating && lazyStaggeredGridStateFromViewModel.canScrollBackward) {
                                item(span = FullLine) {
                                    ShowBottomLoader()
                                }
                            }
                        }
                    }

                    is ContentState.SwitchingFeed -> {
                        CenteredLoader()
                    }

                    is ContentState.FeedFailed -> {
                        InGridStatusMessage(
                            message = content.errorMessage,
                            onRetry = { onChipClicked(wallpaperUIState.selectedFeed) }
                        )
                    }

                    is ContentState.Empty -> {
                        InGridStatusMessage(
                            message = "No images found.",
                            onRetry = onRefresh
                        )
                    }
                }
            }
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState
        )
    }

    LaunchedEffect(Unit) {
        scrollToTopEvents.collect {
            lazyStaggeredGridStateFromViewModel.requestScrollToItem(0)
        }
    }

    LaunchedEffect(wallpaperUIState.statusMessage) {
        wallpaperUIState.statusMessage?.let { statusMessage ->
            snackBarHostState.showSnackbar(
                message = statusMessage,
                duration = SnackbarDuration.Short
            )
            onStatusMessageShown()
        }
    }

    LaunchedEffect(
        lazyStaggeredGridStateFromViewModel,
        (content as? ContentState.Loaded)?.images?.size,
        isPaginating
    ) {
        if(content !is ContentState.Loaded) return@LaunchedEffect
        snapshotFlow {
            val lastVisibleItemIndex = lazyStaggeredGridStateFromViewModel.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val reachedBottom = lastVisibleItemIndex >= (content.images.size - 10)
            reachedBottom && lazyStaggeredGridStateFromViewModel.isScrollInProgress
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && !isPaginating) {
                    onLoadMore()
                }
            }
    }
}

@Composable
fun InGridStatusMessage(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = message, textAlign = TextAlign.Center)
            onRetry?.let {
                SpringyTextButton(
                    buttonText = "Retry",
                    containerColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = it,
                    trailingIcon = Icons.Outlined.Replay
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun ShowBottomLoader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
        ,
        contentAlignment = Alignment.Center,
    ) {
        ContainedLoadingIndicator()
    }
}

@Preview
@Composable
private fun ShowErrorPreview() {
    ShowError(errorMessage = "Network Error")
}