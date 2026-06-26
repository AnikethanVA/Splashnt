package com.ava.splashnt.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ava.splashnt.R

@Composable
fun WordMark(
    modifier: Modifier = Modifier,
    fontSize: TextUnit,
    letterSpacing: TextUnit,
    dotSize: Dp
) {

    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density = density.density, fontScale = 1f)
    ) {
        Row(
            modifier = modifier
        ) {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = stringResource(R.string.app_name),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = letterSpacing,
                ),
            )

            Box(
                modifier = Modifier
                    .alignBy { it.measuredHeight }
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }

}

@Preview(showSystemUi = true)
@Composable
private fun WordmarkPreview() {
    WordMark(
        fontSize = 56.sp,
        letterSpacing = (-2.5f).sp,
        dotSize = 15.dp
    )
}