package com.ava.splashnt.ui.home

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import com.ava.splashnt.ui.home.WallpaperUIState.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when(uiState) {
        is Loading -> {
            ShowLoader()
        }
        is Error -> {
            ShowError(errorMessage = (uiState as Error).errorMessage)
        }

        is Success -> {
            ShowWallpapers(wallpaperUIState = (uiState as Success))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShowLoader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ShowError(
    modifier: Modifier = Modifier,
    errorMessage: String
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
    wallpaperUIState: WallpaperUIState.Success
) {

}

@Preview(showSystemUi = true)
@Composable
private fun ShowErrorPreview() {
    ShowError(errorMessage = "Network Error")
}