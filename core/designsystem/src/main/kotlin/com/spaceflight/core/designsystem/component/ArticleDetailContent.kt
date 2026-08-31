package com.spaceflight.core.designsystem.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spaceflight.core.designsystem.R

private val HeroHeight = 280.dp

/**
 * The article detail body.
 *
 * SNAPI only exposes a summary, never the article text, so this stops at the teaser and hands the
 * reader off to the publisher. The hero drifts at half the scroll speed for depth, and its headline
 * fades out as it leaves so the caller's top bar can pick the title up.
 */
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
    scrollState: ScrollState = rememberScrollState(),
    imageModifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
) {
    // A fixed hero would swallow a landscape phone whole, so it never takes more than a bit under
    // half the window.
    val windowHeight = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    val heroHeight = HeroHeight.coerceAtMost(windowHeight * 0.42f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heroHeight),
        ) {
            AsyncArticleImage(
                imageUrl = imageUrl,
                contentDescription = null,
                shape = RectangleShape,
                modifier = imageModifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = scrollState.value * ParallaxFactor },
            )

            Box(Modifier.fillMaxSize().scrim())

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
                    .graphicsLayer { alpha = heroTitleAlpha(scrollState.value) },
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SourceBadge(
                    newsSite = newsSite,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = titleModifier,
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
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
                        AssistChip(
                            onClick = {},
                            enabled = false,
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.RocketLaunch,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            label = {
                                Text(
                                    pluralStringResource(
                                        R.plurals.ds_related_launches,
                                        launchCount,
                                        launchCount,
                                    )
                                )
                            },
                        )
                    }
                    if (eventCount > 0) {
                        AssistChip(
                            onClick = {},
                            enabled = false,
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Event,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            label = {
                                Text(
                                    pluralStringResource(
                                        R.plurals.ds_related_events,
                                        eventCount,
                                        eventCount,
                                    )
                                )
                            },
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
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = null,
                    Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.ds_read_full_article))
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = stringResource(R.string.ds_attribution),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Fully opaque until the hero starts leaving, then gone by the time it is halfway out. */
fun heroTitleAlpha(scrollOffset: Int): Float =
    (1f - scrollOffset / 320f).coerceIn(0f, 1f)

@Composable
private fun buildMetaLine(authors: List<String>, dateLabel: String): String {
    val byline = authors.takeIf { it.isNotEmpty() }?.joinToString(", ")
    return if (byline == null) {
        dateLabel
    } else {
        stringResource(R.string.ds_byline, byline) + "  ·  " + dateLabel
    }
}

private const val ParallaxFactor = 0.5f
