package com.spaceflight.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A full snapshot of a favourited article. Deliberately not a foreign key into `articles`: the feed
 * cache is cleared on every refresh, while favourites must survive that and stay readable offline.
 */
@Entity(tableName = "favorite_articles")
data class FavoriteArticleEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val summary: String,
    val imageUrl: String,
    val newsSite: String,
    val url: String,
    val authors: List<String>,
    val publishedAt: Long,
    val isFeatured: Boolean,
    val launchCount: Int,
    val eventCount: Int,
    val favoritedAt: Long,
)
