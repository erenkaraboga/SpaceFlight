package com.spaceflight.core.domain.model

import java.time.Instant

/**
 * A spaceflight news article as the rest of the app understands it.
 *
 * The Spaceflight News API never returns the full body of an article, only a [summary] plus the
 * [url] of the publisher's page, which is why the detail screen links out instead of rendering text.
 */
data class Article(
    val id: Int,
    val title: String,
    val summary: String,
    val imageUrl: String,
    val newsSite: String,
    val url: String,
    val authors: List<String>,
    val publishedAt: Instant,
    val updatedAt: Instant,
    val isFeatured: Boolean,
    val launchCount: Int,
    val eventCount: Int,
)
