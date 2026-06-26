package com.ava.splashnt.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ava.splashnt.ui.common.WordMark

@Composable
fun SplashContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {

        val primary = MaterialTheme.colorScheme.primary
        val primaryContainer = MaterialTheme.colorScheme.primaryContainer

        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.32f)
                .blur(72.dp)
                .drawWithCache {
                    val brush = Brush.radialGradient(
                        0.0f to primary,
                        0.20f to primary,
                        0.30f to primaryContainer,
                        0.30f to Color.Transparent,
                        1.0f to Color.Transparent,
                        radius = size.maxDimension,
                    )
                    onDrawBehind { drawRect(brush) }
                }
        )

        WordMark(
            fontSize = 56.sp,
            letterSpacing = (-2.5f).sp,
            dotSize = 15.dp
        )
    }
}


@Preview(showSystemUi = true)
@Composable
private fun SplashContentPreview() {
    SplashContent()
}