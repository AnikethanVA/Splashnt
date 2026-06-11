package com.ava.splashnt.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ava.splashnt.data.model.Topic

private const val ALL_CHIP_KEY = "__all__"

@Composable
fun TopicChipRow(
    selectedFeed: FeedSelection,
    availableTopics: List<Topic>,
    onChipClicked: (FeedSelection) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            vertical = 8.dp,
            horizontal = 20.dp
        )
    ) {
        item(
            key = ALL_CHIP_KEY
        ) {
            TopicChip(
                selected = selectedFeed is FeedSelection.All,
                onClick = { onChipClicked(FeedSelection.All) },
                label = "All"
            )
        }

        items(
            items = availableTopics,
            key = { it.id }
        ) { topic ->
            TopicChip(
                selected = selectedFeed is FeedSelection.Topic && topic.slug == selectedFeed.slug,
                onClick = { onChipClicked(FeedSelection.Topic(slug = topic.slug, displayName = topic.title)) },
                label = topic.title
            )
        }
    }
}

@Composable
private fun TopicChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        shape = RoundedCornerShape(percent = 50),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(12.dp),
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    )
}

@Preview(showSystemUi = true)
@Composable
private fun TopicChipRowPreview() {
    TopicChipRow(
        selectedFeed = FeedSelection.All,
        availableTopics = listOf(
            Topic("id-1", "nature", "Nature"),
            Topic("id-2", "spring", "Spring"),
            Topic("id-3", "wallpapers", "Wallpapers"),
            Topic("id-4", "film", "Film"),
            Topic("id-5", "textures", "Textures"),
        ),
        onChipClicked = {}
    )

}