package com.spaceflight.core.data.repository

import app.cash.turbine.test
import com.spaceflight.core.data.fake.FakeFavoriteArticleDao
import com.spaceflight.core.model.AppError
import com.spaceflight.core.model.Article
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.time.Instant

class FavoriteRepositoryImplTest {

    private val dao = FakeFavoriteArticleDao()
    private val repository = FavoriteRepositoryImpl(dao)

    @Test
    fun `toggling an unfavorited article stores it and reports it as favorited`() = runTest {
        val added = repository.toggleFavorite(article(1)).getOrThrow()

        assertTrue(added)
        assertEquals(listOf(1), repository.observeFavoriteIds().first().toList())
    }

    @Test
    fun `toggling a favorited article removes it again`() = runTest {
        repository.toggleFavorite(article(1))

        val stillFavorited = repository.toggleFavorite(article(1)).getOrThrow()

        assertFalse(stillFavorited)
        assertTrue(repository.observeFavorites().first().isEmpty())
    }

    @Test
    fun `the full article is kept so favorites survive a cache wipe`() = runTest {
        val original = article(7).copy(summary = "A long summary", authors = listOf("Ada"))

        repository.toggleFavorite(original)

        assertEquals(original, repository.observeFavorites().first().single())
    }

    @Test
    fun `favorite ids emit again whenever the set changes`() = runTest {
        repository.observeFavoriteIds().test {
            assertEquals(emptySet<Int>(), awaitItem())

            repository.toggleFavorite(article(1))
            assertEquals(setOf(1), awaitItem())

            repository.removeFavorite(1)
            assertEquals(emptySet<Int>(), awaitItem())
        }
    }

    @Test
    fun `a single article reports its own favorite state`() = runTest {
        repository.observeIsFavorite(3).test {
            assertFalse(awaitItem())

            repository.toggleFavorite(article(3))
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `a dao failure while toggling a favorite is reported as a Result failure, not a crash`() = runTest {
        dao.failNextWrite(IOException("disk full"))

        val result = repository.toggleFavorite(article(1))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.NoConnection)
        assertTrue(repository.observeFavorites().first().isEmpty())
    }

    private fun article(id: Int) = Article(
        id = id,
        title = "Article $id",
        summary = "Summary $id",
        imageUrl = "https://example.com/$id.jpg",
        newsSite = "Test Site",
        url = "https://example.com/$id",
        authors = emptyList(),
        publishedAt = Instant.ofEpochMilli(id.toLong()),
        isFeatured = false,
        launchCount = 0,
        eventCount = 0,
    )
}
