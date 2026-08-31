package com.spaceflight.core.data.mapper

import com.spaceflight.core.data.database.entity.ArticleEntity
import com.spaceflight.core.data.database.entity.FavoriteArticleEntity
import com.spaceflight.core.data.network.dto.ArticleDto
import com.spaceflight.core.domain.model.Article
import java.time.Instant
import java.time.format.DateTimeParseException

fun ArticleDto.toEntity(): ArticleEntity = ArticleEntity(
    id = id,
    title = title.orEmpty().trim(),
    summary = summary.cleanSummary(),
    imageUrl = imageUrl.orEmpty(),
    newsSite = newsSite.orEmpty(),
    url = url.orEmpty(),
    authors = authors.mapNotNull { it.name?.trim()?.takeIf(String::isNotEmpty) },
    publishedAt = publishedAt.toEpochMillis(),
    updatedAt = updatedAt.toEpochMillis(),
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
    updatedAt = Instant.ofEpochMilli(updatedAt),
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
    updatedAt = Instant.ofEpochMilli(updatedAt),
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
    updatedAt = updatedAt.toEpochMilli(),
    isFeatured = isFeatured,
    launchCount = launchCount,
    eventCount = eventCount,
    favoritedAt = favoritedAt,
)

fun ArticleEntity.toFavoriteEntity(favoritedAt: Long): FavoriteArticleEntity =
    toDomain().toFavoriteEntity(favoritedAt)

/**
 * Publishers push their WordPress footer straight into the API summary, so strip the trailing
 * "The post ... appeared first on ..." line and normalise the stray newlines around it.
 */
internal fun String?.cleanSummary(): String {
    val text = this?.trim().orEmpty()
    if (text.isEmpty()) return ""
    return text
        .replace(POST_FOOTER, "")
        .replace(EXCESS_BLANK_LINES, "\n\n")
        .trim()
}

/**
 * `published_at` comes back with second precision and `updated_at` with microseconds; [Instant]
 * parses both. Anything unexpected degrades to the epoch rather than dropping the article.
 */
internal fun String?.toEpochMillis(): Long = try {
    this?.let { Instant.parse(it).toEpochMilli() } ?: 0L
} catch (_: DateTimeParseException) {
    0L
}

private val POST_FOOTER =
    Regex("""\s*The post\b[^\n]*?appeared first on[^\n]*$""", RegexOption.IGNORE_CASE)

private val EXCESS_BLANK_LINES = Regex("""\n{3,}""")
