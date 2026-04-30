package com.ava.splashnt.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.Companion.FullLine
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ava.splashnt.data.model.UnsplashModel
import com.ava.splashnt.ui.home.WallpaperUIState.Error
import com.ava.splashnt.ui.home.WallpaperUIState.Loading
import com.ava.splashnt.ui.home.WallpaperUIState.Success
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onImageClicked: (UnsplashModel) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when(val state = uiState) {
        is Loading -> {
            ShowLoader(modifier)
        }
        is Error -> {
            ShowError(modifier = modifier, errorMessage = state.errorMessage)
        }

        is Success -> {
            ShowWallpapers(
                modifier = modifier,
                wallpaperUIState = state,
                lazyStaggeredGridStateFromViewModel = viewModel.lazyStaggeredGridState,
                onLoadMore = viewModel::loadMoreImages,
                onRefresh = viewModel::onRefresh,
                onStatusMessageShown = viewModel::onStatusMessageShown,
                onImageClicked = onImageClicked,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showSystemUi = true)
@Composable
fun ShowLoader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ContainedLoadingIndicator()
    }
}

@Composable
fun ShowError(
    modifier: Modifier = Modifier,
    errorMessage: String
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "An error occurred: $errorMessage",
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
    onStatusMessageShown: () -> Unit,
    onImageClicked: (UnsplashModel) -> Unit
) {

    val snackBarHostState = remember { SnackbarHostState() }

    PullToRefreshBox(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp),
        isRefreshing = wallpaperUIState.isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyVerticalStaggeredGrid(
            state = lazyStaggeredGridStateFromViewModel,
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = wallpaperUIState.images, key = { it.id }) { image ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(image.width.toFloat()/image.height),
                    onClick = {
                        onImageClicked(image)
                    },
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

            if (wallpaperUIState.isPaginating && lazyStaggeredGridStateFromViewModel.canScrollBackward) {
                item(span = FullLine) {
                    ShowBottomLoader()
                }
            }
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState
        )
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

    LaunchedEffect(lazyStaggeredGridStateFromViewModel, wallpaperUIState.images.size, wallpaperUIState.isPaginating) {
        snapshotFlow {
            val lastVisibleItemIndex = lazyStaggeredGridStateFromViewModel.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val reachedBottom = lastVisibleItemIndex >= (wallpaperUIState.images.size - 10)
            reachedBottom && lazyStaggeredGridStateFromViewModel.isScrollInProgress
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && !wallpaperUIState.isPaginating) {
                    onLoadMore()
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
            .padding(vertical = 8.dp)
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