package com.spaceflight.core.data.repository

import androidx.paging.testing.asSnapshot
import com.spaceflight.core.database.SpaceflightDatabase
import com.spaceflight.core.database.entity.ArticleEntity
import com.spaceflight.core.data.fake.FakeArticleDao
import com.spaceflight.core.data.fake.FakeFavoriteArticleDao
import com.spaceflight.core.data.fake.FakeNetworkMonitor
import com.spaceflight.core.data.fake.FakeSpaceflightApi
import com.spaceflight.core.data.mapper.toFavoriteEntity
import com.spaceflight.core.network.dto.ArticleDto
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticleRepositoryImplTest {

    private val articleDao = FakeArticleDao(
        listOf(
            entity(id = 1, title = "Starship completes static fire"),
            entity(id = 2, title = "Artemis rolls out to the pad"),
        )
    )
    private val favoriteDao = FakeFavoriteArticleDao()
    private val networkMonitor = FakeNetworkMonitor()
    private val api = FakeSpaceflightApi(articles = listOf(ArticleDto(id = 3, title = "Remote")))

    private fun repository() = ArticleRepositoryImpl(
        api = api,
        // Only the RemoteMediator touches the database, and none of these cases go through it.
        database = mockk<SpaceflightDatabase>(relaxed = true),
        articleDao = articleDao,
        favoriteDao = favoriteDao,
        networkMonitor = networkMonitor,
    )

    @Test
    fun `searching offline falls back to a local match over the cached feed`() = runTest {
        networkMonitor.setOnline(false)

        val results = repository().getArticles("starship").asSnapshot()

        assertEquals(listOf(1), results.map { it.id })
        assertNull("The API must not be called while offline", api.lastSearch)
    }

    @Test
    fun `searching offline matches the summary as well as the title`() = runTest {
        networkMonitor.setOnline(false)

        val results = repository().getArticles("static fire").asSnapshot()

        assertEquals(listOf(1), results.map { it.id })
    }

    @Test
    fun `searching online caches hits so a later detail screen can read them`() = runTest {
        val results = repository().getArticles("remote").asSnapshot()

        assertEquals(listOf(3), results.map { it.id })
        assertEquals("remote", api.lastSearch)
        assertEquals("Remote", articleDao.stored.first { it.id == 3 }.title)
    }

    @Test
    fun `an article missing from the feed cache is still readable from favorites`() = runTest {
        val onlyFavorited = entity(id = 99, title = "Saved earlier")
        favoriteDao.upsert(onlyFavorited.toFavoriteEntity(favoritedAt = 1L))

        val article = repository().observeArticle(99).first()

        assertEquals("Saved earlier", article?.title)
    }

    @Test
    fun `observing an article the app has never seen emits null`() = runTest {
        assertNull(repository().observeArticle(404).first())
    }

    @Test
    fun `refreshing updates a cached article in place`() = runTest {
        val api = FakeSpaceflightApi(
            articles = listOf(ArticleDto(id = 1, title = "Starship completes static fire (updated)"))
        )
        val repository = ArticleRepositoryImpl(
            api = api,
            database = mockk<SpaceflightDatabase>(relaxed = true),
            articleDao = articleDao,
            favoriteDao = favoriteDao,
            networkMonitor = networkMonitor,
        )

        val result = repository.refreshArticle(1)

        assertTrue(result.isSuccess)
        assertEquals(
            "Starship completes static fire (updated)",
            articleDao.stored.first { it.id == 1 }.title,
        )
    }

    @Test
    fun `refreshing inserts an article the feed has never cached`() = runTest {
        val result = repository().refreshArticle(3)

        assertTrue(result.isSuccess)
        assertEquals("Remote", articleDao.stored.first { it.id == 3 }.title)
    }

    private fun entity(id: Int, title: String) = ArticleEntity(
        id = id,
        title = title,
        summary = title,
        imageUrl = "",
        newsSite = "Test Site",
        url = "https://example.com/$id",
        authors = emptyList(),
        publishedAt = id.toLong(),
        updatedAt = id.toLong(),
        isFeatured = false,
        launchCount = 0,
        eventCount = 0,
    )
}
