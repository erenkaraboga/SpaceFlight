package com.spaceflight.core.data.mapper

import com.spaceflight.core.database.entity.ArticleEntity
import com.spaceflight.core.database.entity.FavoriteArticleEntity
import com.spaceflight.core.network.dto.ArticleDto
import com.spaceflight.core.model.Article
import java.time.Instant
import java.time.format.DateTimeParseException

fun ArticleDto.toEntity(): ArticleEntity = ArticleEntity(
    id = id,
    title = title.orEmpty().trim(),
    summary = summary.orEmpty().cleanedSummary(),
    imageUrl = imageUrl.orEmpty(),
    newsSite = newsSite.orEmpty(),
    url = url.orEmpty(),
    authors = authors.mapNotNull { it.name?.trim()?.takeIf(String::isNotEmpty) },
    publishedAt = publishedAt.toEpochMillis(),
    isFeatured = featured == true,
    launchCount = launches.size,
    eventCount = events.size,
)

fun ArticleDto.toDomain(): Article = toEntity().toDomain()

fun ArticleEntity.toDomain(): Article = Article(
    id = id,
    title = title,
    summary = summary,
    imageUrl = imageUrl,
    newsSite = newsSite,
    url = url,
    authors = authors,
    publishedAt = Instant.ofEpochMilli(publishedAt),
    isFeatured = isFeatured,
    launchCount = launchCount,
    eventCount = eventCount,
)

fun FavoriteArticleEntity.toDomain(): Article = Article(
    id = id,
    title = title,
    summary = summary,
    imageUrl = imageUrl,
    newsSite = newsSite,
    url = url,
    authors = authors,
    publishedAt = Instant.ofEpochMilli(publishedAt),
    isFeatured = isFeatured,
    launchCount = launchCount,
    eventCount = eventCount,
)

fun Article.toFavoriteEntity(favoritedAt: Long): FavoriteArticleEntity = FavoriteArticleEntity(
    id = id,
    title = title,
    summary = summary,
    imageUrl = imageUrl,
    newsSite = newsSite,
    url = url,
    authors = authors,
    publishedAt = publishedAt.toEpochMilli(),
    isFeatured = isFeatured,
    launchCount = launchCount,
    eventCount = eventCount,
    favoritedAt = favoritedAt,
)

fun ArticleEntity.toFavoriteEntity(favoritedAt: Long): FavoriteArticleEntity =
    toDomain().toFavoriteEntity(favoritedAt)

/**
 * Publishers often append "The post … appeared first on …" plus leading/trailing newlines. Readers
 * never asked for that, so it is stripped before the summary is stored.
 */
private fun String.cleanedSummary(): String =
    PUBLISHER_FOOTER.replace(this, "").trim()

private val PUBLISHER_FOOTER = Regex(
    """\s*The post .+ appeared first on .+\.?\s*$""",
    RegexOption.IGNORE_CASE,
)

/**
 * `published_at` comes back with second precision and `updated_at` with microseconds; [Instant]
 * parses both. Anything unexpected degrades to the epoch rather than dropping the article.
 */
internal fun String?.toEpochMillis(): Long = try {
    this?.let { Instant.parse(it).toEpochMilli() } ?: 0L
} catch (_: DateTimeParseException) {
    0L
}
