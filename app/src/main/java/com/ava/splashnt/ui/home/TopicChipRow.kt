package com.ava.splashnt.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import com.ava.splashnt.data.model.UnsplashTopic

private const val ALL_CHIP_KEY = "__all__"

@Composable
fun TopicChipRow(
    selectedFeed: FeedSelection,
    availableTopics: List<UnsplashTopic>,
    onChipClicked: (FeedSelection) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            vertical = 8.dp,
            horizontal = 20.dp
        )
    ) {
        item(
            key = ALL_CHIP_KEY
        ) {

            val isNoTopicSelected = selectedFeed is FeedSelection.All
            FilterChip(
                selected = isNoTopicSelected,
                onClick = { onChipClicked(FeedSelection.All) },
                label = { Text("All") },
                shape = RoundedCornerShape(percent = 50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim,
                ),
                contentPadding = PaddingValues(12.dp),
                border = if(isNoTopicSelected) {
                    BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondaryContainer)
                } else {
                    BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline)
                }
            )
        }

        items(
            items = availableTopics,
            key = { it.id }
        ) { topic ->
            Spacer(modifier = Modifier.width(8.dp))

            val isTopicSelected = selectedFeed is FeedSelection.Topic && topic.slug == selectedFeed.slug

            FilterChip(
                selected = isTopicSelected,
                onClick = { onChipClicked(FeedSelection.Topic(slug = topic.slug, displayName = topic.title)) },
                label = { Text(text = topic.title) },
                shape = RoundedCornerShape(percent = 50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim,
                ),
                contentPadding = PaddingValues(12.dp),
                border = if(isTopicSelected) {
                        BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondaryContainer)
                    } else {
                        BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline)
                    }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun TopicChipRowPreview() {
    TopicChipRow(
        selectedFeed = FeedSelection.All,
        availableTopics = listOf(
            UnsplashTopic("id-1", "nature", "Nature"),
            UnsplashTopic("id-2", "spring", "Spring"),
            UnsplashTopic("id-3", "wallpapers", "Wallpapers"),
            UnsplashTopic("id-4", "film", "Film"),
            UnsplashTopic("id-5", "textures", "Textures"),
        ),
        onChipClicked = {}
    )

}