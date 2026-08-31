package com.spaceflight.core.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedArticlesDto(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<ArticleDto> = emptyList(),
)

/**
 * Mirrors the `Article` schema of SNAPI v4. Almost everything carries a default because the live API
 * is looser than its OpenAPI document: `socials` is documented as an object yet arrives as `null`,
 * and `featured` is absent from the schema's required list.
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
    @SerialName("updated_at") val updatedAt: String? = null,
    val featured: Boolean? = null,
    val launches: List<LaunchDto> = emptyList(),
    val events: List<EventDto> = emptyList(),
)

@Serializable
data class AuthorDto(
    val name: String? = null,
    val socials: SocialsDto? = null,
)

@Serializable
data class SocialsDto(
    val x: String? = null,
    val youtube: String? = null,
    val instagram: String? = null,
    val linkedin: String? = null,
    val mastodon: String? = null,
    val bluesky: String? = null,
)

@Serializable
data class LaunchDto(
    @SerialName("launch_id") val launchId: String? = null,
    val provider: String? = null,
)

@Serializable
data class EventDto(
    @SerialName("event_id") val eventId: Int? = null,
    val provider: String? = null,
)
