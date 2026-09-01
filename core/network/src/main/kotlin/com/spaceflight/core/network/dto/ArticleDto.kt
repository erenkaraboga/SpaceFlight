package com.spaceflight.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedArticlesDto(
    val next: String? = null,
    val results: List<ArticleDto> = emptyList(),
)

/**
 * Mirrors the `Article` schema of SNAPI v4. Almost everything carries a default because the live API
 * is looser than its OpenAPI document, and `featured` is absent from the schema's required list.
 *
 * Only what the app actually shows is modeled here: `count`/`previous` (unused pagination fields),
 * `updated_at`, per-author `socials`, and the per-launch/per-event fields all get parsed straight
 * past by `ignoreUnknownKeys` -- [launches] and [events] exist only to be counted.
 */
@Serializable
data class ArticleDto(
    val id: Int,
    val title: String? = null,
    val authors: List<AuthorDto> = emptyList(),
    val url: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("news_site") val newsSite: String? = null,
    val summary: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    val featured: Boolean? = null,
    val launches: List<LaunchDto> = emptyList(),
    val events: List<EventDto> = emptyList(),
)

@Serializable
data class AuthorDto(
    val name: String? = null,
)

/** Only the list size is ever read (see [ArticleDto.launches]); per-launch fields aren't modeled. */
@Serializable
class LaunchDto

/** Only the list size is ever read (see [ArticleDto.events]); per-event fields aren't modeled. */
@Serializable
class EventDto
