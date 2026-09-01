package com.spaceflight.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The feed is a single offset-paginated stream, so one row is enough to remember where the next
 * APPEND should continue from.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val id: Int = FEED_KEY_ID,
    val nextOffset: Int,
    val endReached: Boolean,
    val lastRefreshedAt: Long,
) {
    companion object {
        const val FEED_KEY_ID = 1
    }
}
