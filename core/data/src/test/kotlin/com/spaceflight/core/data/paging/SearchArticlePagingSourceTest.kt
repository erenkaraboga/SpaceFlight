package com.spaceflight.core.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.spaceflight.core.data.fake.FakeArticleDao
import com.spaceflight.core.data.fake.FakeSpaceflightApi
import com.spaceflight.core.model.AppError
import com.spaceflight.core.network.dto.ArticleDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * Search results also get written into the feed cache (see [SearchArticlePagingSource]'s own kdoc),
 * so a later tap-through to the detail screen can read the same article the search list showed --
 * this is the one piece of that contract this suite checks; [ArticleMapperTest] and
 * [com.spaceflight.core.data.repository.ArticleRepositoryImplTest] cover the mapping/offline pieces.
 */
class SearchArticlePagingSourceTest {

    private val config = PagingConfig(pageSize = 2, initialLoadSize = 2, enablePlaceholders = false)
    private val articleDao = FakeArticleDao()

    private fun pagingSource(api: FakeSpaceflightApi, query: String = "starship") =
        SearchArticlePagingSource(api = api, articleDao = articleDao, query = query, pageSize = 2)

    @Test
    fun `a page of results is mapped to domain articles and cached for later`() = runTest {
        val api = FakeSpaceflightApi(
            articles = listOf(ArticleDto(id = 1, title = "Starship static fire"), ArticleDto(id = 2)),
        )
        val pager = TestPager(config, pagingSource(api))

        val result = pager.refresh() as PagingSource.LoadResult.Page

        assertEquals(listOf(1, 2), result.data.map { it.id })
        assertEquals("Starship static fire", result.data.first().title)
        assertEquals(listOf(1, 2), articleDao.stored.map { it.id }.sorted())
    }

    @Test
    fun `the query is forwarded to the api as a search`() = runTest {
        val api = FakeSpaceflightApi(articles = listOf(ArticleDto(id = 1)))

        TestPager(config, pagingSource(api, query = "artemis")).refresh()

        assertEquals("artemis", api.lastSearch)
    }

    @Test
    fun `paging continues from where the previous page left off`() = runTest {
        val api = FakeSpaceflightApi(
            articles = (1..5).map { ArticleDto(id = it, title = "Article $it") },
        )
        val pager = TestPager(config, pagingSource(api))

        pager.refresh()
        val second = pager.append() as PagingSource.LoadResult.Page

        assertEquals(listOf(3, 4), second.data.map { it.id })
    }

    @Test
    fun `pagination ends once the api stops returning a next page`() = runTest {
        val api = FakeSpaceflightApi(articles = listOf(ArticleDto(id = 1), ArticleDto(id = 2)))
        val pager = TestPager(config, pagingSource(api))

        val result = pager.refresh() as PagingSource.LoadResult.Page

        assertNull("everything fit on one page, so there is nothing left to append", result.nextKey)
    }

    @Test
    fun `an empty page is not written into the cache`() = runTest {
        val api = FakeSpaceflightApi(articles = emptyList())

        TestPager(config, pagingSource(api)).refresh()

        assertTrue(articleDao.stored.isEmpty())
    }

    @Test
    fun `a network failure surfaces as a mapped error instead of crashing the list`() = runTest {
        val api = FakeSpaceflightApi(articles = emptyList(), failure = IOException("offline"))
        val pager = TestPager(config, pagingSource(api))

        val result = pager.refresh() as PagingSource.LoadResult.Error

        assertTrue(result.throwable is AppError.NoConnection)
    }
}
