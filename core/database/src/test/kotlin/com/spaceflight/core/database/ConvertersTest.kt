package com.spaceflight.core.database

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The author list is the one column Room can't store natively, so it goes through a hand-written
 * converter -- a bug here silently corrupts every article's byline, so it earns a direct test
 * rather than relying on it being exercised incidentally through a DAO.
 */
class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `an empty author list converts to an empty string`() {
        assertEquals("", converters.fromAuthors(emptyList()))
    }

    @Test
    fun `an empty string converts back to an empty list, not a list of one blank string`() {
        assertEquals(emptyList<String>(), converters.toAuthors(""))
    }

    @Test
    fun `a single author round trips`() {
        val authors = listOf("Ada Lovelace")

        assertEquals(authors, converters.toAuthors(converters.fromAuthors(authors)))
    }

    @Test
    fun `multiple authors round trip in order`() {
        val authors = listOf("Ada Lovelace", "Grace Hopper", "Katherine Johnson")

        assertEquals(authors, converters.toAuthors(converters.fromAuthors(authors)))
    }

    @Test
    fun `author names containing commas are preserved, not mistaken for a separator`() {
        val authors = listOf("Smith, John", "Doe, Jane")

        assertEquals(authors, converters.toAuthors(converters.fromAuthors(authors)))
    }
}
