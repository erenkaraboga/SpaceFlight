package com.spaceflight.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spaceflight.core.database.dao.ArticleDao
import com.spaceflight.core.database.dao.FavoriteArticleDao
import com.spaceflight.core.database.dao.RemoteKeyDao
import com.spaceflight.core.database.entity.ArticleEntity
import com.spaceflight.core.database.entity.FavoriteArticleEntity
import com.spaceflight.core.database.entity.RemoteKeyEntity
import com.spaceflight.core.database.migration.AutoMigration1To2Spec
import com.spaceflight.core.database.migration.AutoMigration2To3Spec

@Database(
    entities = [
        ArticleEntity::class,
        FavoriteArticleEntity::class,
        RemoteKeyEntity::class,
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = AutoMigration1To2Spec::class),
        AutoMigration(from = 2, to = 3, spec = AutoMigration2To3Spec::class),
    ],
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
