package com.spaceflight.feature.newsdetail.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.component.PillBadge
import com.spaceflight.designsystem.component.RemoteImage
import com.spaceflight.designsystem.theme.imageScrim
import com.spaceflight.feature.newsdetail.R

private val PortraitHeroHeight = 420.dp

@Composable
fun ArticleDetailContent(
    title: String,
    summary: String,
    imageUrl: String,
    newsSite: String,
    authors: List<String>,
    dateLabel: String,
    launchCount: Int,
    eventCount: Int,
    onReadMore: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    val windowHeight = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    val isLandscape = LocalWindowInfo.current.containerSize.width >
        LocalWindowInfo.current.containerSize.height
    val heroHeight = if (isLandscape) {
        windowHeight * 0.55f
    } else {
        PortraitHeroHeight.coerceAtMost(windowHeight * 0.55f)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heroHeight)
                .clip(RectangleShape)
                .then(imageModifier),
        ) {
            RemoteImage(
                imageUrl = imageUrl,
                contentDescription = null,
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = scrollState.value * ParallaxFactor },
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(imageScrim)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
                    .graphicsLayer { alpha = heroTitleAlpha(scrollState.value) },
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PillBadge(
                    text = newsSite,
                    containerColor = Color.White.copy(alpha = 0.18f),
                    contentColor = Color.White,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = buildMetaLine(authors, dateLabel),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (launchCount > 0 || eventCount > 0) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (launchCount > 0) {
                        RelatedChip(
                            icon = Icons.Rounded.RocketLaunch,
                            label = pluralStringResource(
                                R.plurals.newsdetail_related_launches,
                                launchCount,
                                launchCount,
                            ),
                        )
                    }
                    if (eventCount > 0) {
                        RelatedChip(
                            icon = Icons.Rounded.Event,
                            label = pluralStringResource(
                                R.plurals.newsdetail_related_events,
                                eventCount,
                                eventCount,
                            ),
                        )
                    }
                }
            }

            if (summary.isNotBlank()) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Button(
                onClick = onReadMore,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = null,
                    Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.newsdetail_read_full_article))
            }

            Text(
                text = stringResource(R.string.newsdetail_attribution),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

fun heroTitleAlpha(scrollOffset: Int): Float =
    (1f - scrollOffset / 280f).coerceIn(0f, 1f)

@Composable
private fun RelatedChip(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun buildMetaLine(authors: List<String>, dateLabel: String): String {
    val byline = authors.takeIf { it.isNotEmpty() }?.joinToString(", ")
    return if (byline == null) {
        dateLabel
    } else {
        stringResource(R.string.newsdetail_byline, byline) + "  ·  " + dateLabel
    }
}

private const val ParallaxFactor = 0.45f
