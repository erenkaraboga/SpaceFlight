package com.spaceflight.feature.news.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.component.rememberStaggeredEntranceState
import com.spaceflight.designsystem.component.staggeredEntrance

@Composable
fun NewsLoading(
    modifier: Modifier = Modifier,
) {
    val entranceState = rememberStaggeredEntranceState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        HeroArticleCardPlaceholder(Modifier.staggeredEntrance(0, entranceState))
        repeat(PlaceholderCount) {
            ArticleCardPlaceholder(Modifier.staggeredEntrance(it + 1, entranceState))
        }
    }
}
private const val PlaceholderCount = 5
