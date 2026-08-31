package com.spaceflight.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spaceflight.core.data.database.dao.ArticleDao
import com.spaceflight.core.data.database.dao.FavoriteArticleDao
import com.spaceflight.core.data.database.dao.RemoteKeyDao
import com.spaceflight.core.data.database.entity.ArticleEntity
import com.spaceflight.core.data.database.entity.FavoriteArticleEntity
import com.spaceflight.core.data.database.entity.RemoteKeyEntity

@Database(
    entities = [
        ArticleEntity::class,
        FavoriteArticleEntity::class,
        RemoteKeyEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class SpaceflightDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun favoriteArticleDao(): FavoriteArticleDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        const val NAME = "spaceflight.db"
    }
}
