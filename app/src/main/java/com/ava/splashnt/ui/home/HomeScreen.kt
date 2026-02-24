package com.ava.splashnt.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan.Companion.FullLine
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    LazyVerticalStaggeredGrid(
        modifier = modifier.padding(horizontal = 8.dp),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(wallpaperUIState.images) { index, image ->
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

            if(!wallpaperUIState.isPaginating && index == (wallpaperUIState.images.size - 5)) {
                onLoadMore()
            }
        }

        if(wallpaperUIState.isPaginating) {
            item(span = FullLine) {
                ShowBottomLoader()
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