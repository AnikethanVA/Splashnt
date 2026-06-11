package com.ava.splashnt.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ava.splashnt.R
import com.ava.splashnt.data.repository.WallpaperSource
import kotlinx.coroutines.launch

@Composable
fun SourcePill(
    modifier: Modifier = Modifier,
    currentSource: WallpaperSource,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        onClick = onClick,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentSource.sourceName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcePickerSheet(
    currentSource: WallpaperSource,
    onClickSource: (WallpaperSource) -> Unit,
    onDismiss: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState()
    val sheetScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        dragHandle = null
    ) {

        BottomSheetDefaults.DragHandle(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.source_selection_sheet_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        WallpaperSource.entries.forEach { source ->
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                ),
                trailingContent = {
                    RadioButton(
                        selected = source == currentSource,
                        onClick = null
                    )
                },
                headlineContent = { Text(source.sourceName)},
                modifier = Modifier.clickable {
                    sheetScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if(!sheetState.isVisible) {
                            onDismiss()
                            onClickSource(source)
                        }
                    }
                }
            )
        }

        Spacer(Modifier.navigationBarsPadding())
    }
}


@Preview(showBackground = true)
@Composable
private fun SourcePillPreview() {
    SourcePill(
        currentSource = WallpaperSource.UNSPLASH,
        onClick = {}
    )
}

@Preview(showSystemUi = true)
@Composable
private fun SourcePickerSheetPreview() {
    SourcePickerSheet(
        currentSource = WallpaperSource.UNSPLASH,
        onClickSource = {},
        onDismiss = {}
    )
}