package com.spaceflight.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spaceflight.core.database.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun get(id: Int = RemoteKeyEntity.FEED_KEY_ID): RemoteKeyEntity?

    @Upsert
    suspend fun upsert(key: RemoteKeyEntity)

    @Query("DELETE FROM remote_keys")
    suspend fun clear()
}
