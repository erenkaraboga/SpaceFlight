package com.spaceflight.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cache of the paged "latest articles" feed. Wiped and refilled on every Paging REFRESH. */
@Entity(tableName = "articles")
data class ArticleEntity(
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
)
