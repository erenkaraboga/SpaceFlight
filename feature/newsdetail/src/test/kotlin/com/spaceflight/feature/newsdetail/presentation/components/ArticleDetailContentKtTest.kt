package com.spaceflight.feature.newsdetail.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Test

/** The hero title fade is a plain scroll-offset -> alpha curve, easy to get an off-by-one in. */
class ArticleDetailContentKtTest {

    @Test
    fun `at the top of the scroll the title is fully opaque`() {
        assertEquals(1f, heroTitleAlpha(0), 0f)
    }

    @Test
    fun `the title has faded out completely by 280px of scroll`() {
        assertEquals(0f, heroTitleAlpha(280), 0f)
    }

    @Test
    fun `alpha fades linearly between the top and the 280px threshold`() {
        assertEquals(0.5f, heroTitleAlpha(140), 0f)
    }

    @Test
    fun `alpha never goes negative once scrolled well past the threshold`() {
        assertEquals(0f, heroTitleAlpha(10_000), 0f)
    }

    @Test
    fun `a negative scroll offset never pushes alpha above fully opaque`() {
        assertEquals(1f, heroTitleAlpha(-50), 0f)
    }
}
