package com.ava.splashnt.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.Companion.FullLine
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ava.splashnt.ui.home.WallpaperUIState.Error
import com.ava.splashnt.ui.home.WallpaperUIState.Loading
import com.ava.splashnt.ui.home.WallpaperUIState.Success
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when(uiState) {
        is Loading -> {
            println("CUSTOMTAG - Inside HomeScreen before ShowLoader is called.")
            ShowLoader(modifier)
        }
        is Error -> {
            ShowError(modifier = modifier, errorMessage = (uiState as Error).errorMessage)
        }

        is Success -> {
            ShowWallpapers(modifier = modifier, wallpaperUIState = (uiState as Success), onLoadMore = viewModel::loadMoreImages)
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
    onLoadMore: () -> Unit,
) {

    val lazyStaggeredGridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = wallpaperUIState.images) { image ->
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {},
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

        if (wallpaperUIState.isPaginating && lazyStaggeredGridState.canScrollBackward) {
            item(span = FullLine) {
                println("CUSTOMTAG - Inside HomeScreen before ShowBottomLoader is shown. Image size = ${wallpaperUIState.images.size}")
                ShowBottomLoader()
            }
        }

    }

    LaunchedEffect(
        lazyStaggeredGridState,
        wallpaperUIState.images.size,
        wallpaperUIState.isPaginating
    ) {
        snapshotFlow {
            lazyStaggeredGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                lastVisibleItemIndex?.let { lastVisibleItemIndex ->
                    if(!wallpaperUIState.isPaginating && lastVisibleItemIndex >= (wallpaperUIState.images.size - 1)) {
                        println("CUSTOMTAG - Inside HomeScreen before onLoadMore is called. Index = $lastVisibleItemIndex, Image size = ${wallpaperUIState.images.size}")
                        onLoadMore()
                    }
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