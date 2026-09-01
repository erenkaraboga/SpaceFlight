package com.spaceflight.core.data.mapper

import com.spaceflight.core.network.dto.ArticleDto
import com.spaceflight.core.network.dto.AuthorDto
import com.spaceflight.core.network.dto.LaunchDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ArticleMapperTest {

    @Test
    fun `maps every field of a complete article`() {
        val article = fullDto().toDomain()

        assertEquals(39746, article.id)
        assertEquals("Roman Space Telescope Headed to Deep Space", article.title)
        assertEquals("Space Scout", article.newsSite)
        assertEquals(listOf("Beverly Casillas"), article.authors)
        assertEquals(1, article.launchCount)
        assertEquals(0, article.eventCount)
        assertTrue(article.isFeatured)
    }

    @Test
    fun `keeps named authors and drops nameless ones`() {
        val dto = fullDto().copy(
            authors = listOf(
                AuthorDto(name = "Beverly Casillas"),
                AuthorDto(name = "  "),
                AuthorDto(name = null),
            )
        )

        assertEquals(listOf("Beverly Casillas"), dto.toDomain().authors)
    }

    @Test
    fun `treats an absent featured flag as not featured`() {
        assertFalse(fullDto().copy(featured = null).toDomain().isFeatured)
    }

    @Test
    fun `parses microsecond precision timestamps, truncated to millis`() {
        val article = fullDto().copy(
            publishedAt = "2026-08-31T15:05:59.462497Z",
        ).toDomain()

        // Timestamps are cached as epoch milliseconds, so the API's microseconds are truncated -
        // harmless for a screen that shows "2 hours ago" and a formatted date.
        assertEquals(Instant.parse("2026-08-31T15:05:59.462Z"), article.publishedAt)
    }

    @Test
    fun `falls back to the epoch rather than dropping an article with a broken date`() {
        val article = fullDto().copy(publishedAt = "not-a-date").toDomain()

        assertEquals(Instant.EPOCH, article.publishedAt)
    }

    @Test
    fun `strips the publisher footer and surrounding whitespace from the summary`() {
        val dto = fullDto().copy(
            summary = "\nThe observatory will shed new light on dark energy.\n\n\n" +
                "The post Roman Space Telescope Headed to Deep Space appeared first on SpaceNews.",
        )

        assertEquals(
            "The observatory will shed new light on dark energy.",
            dto.toDomain().summary,
        )
    }

    @Test
    fun `leaves a summary without a footer untouched`() {
        val summary = "The observatory will shed new light on dark energy."

        assertEquals(summary, fullDto().copy(summary = summary).toDomain().summary)
    }

    @Test
    fun `substitutes empty strings for the nullable text fields`() {
        val article = ArticleDto(id = 1).toDomain()

        assertEquals("", article.title)
        assertEquals("", article.summary)
        assertEquals("", article.imageUrl)
        assertEquals("", article.newsSite)
        assertEquals("", article.url)
        assertEquals(emptyList<String>(), article.authors)
    }

    @Test
    fun `round trips through the favorite entity without losing anything`() {
        val article = fullDto().toDomain()

        assertEquals(article, article.toFavoriteEntity(favoritedAt = 1_000L).toDomain())
    }

    private fun fullDto() = ArticleDto(
        id = 39746,
        title = "Roman Space Telescope Headed to Deep Space",
        authors = listOf(AuthorDto(name = "Beverly Casillas")),
        url = "https://www.spacescout.info/2026/08/roman-space-telescope",
        imageUrl = "https://example.com/header.jpg",
        newsSite = "Space Scout",
        summary = "The observatory will shed new light on dark energy.",
        publishedAt = "2026-08-31T14:54:27Z",
        featured = true,
        launches = listOf(LaunchDto()),
        events = emptyList(),
    )
}
