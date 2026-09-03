package com.spaceflight.core.database.migration

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

/**
 * Room's schema diff sees "a column disappeared" and can't tell a deletion from a rename on its
 * own -- these specs say explicitly which one it is. Each is a plain (no-arg, no dependencies)
 * [AutoMigrationSpec], so Room instantiates it itself; nothing to wire into the database builder.
 */
@DeleteColumn.Entries(
    DeleteColumn(tableName = "articles", columnName = "updatedAt"),
    DeleteColumn(tableName = "favorite_articles", columnName = "updatedAt"),
)
class AutoMigration1To2Spec : AutoMigrationSpec

@DeleteColumn(tableName = "remote_keys", columnName = "lastRefreshedAt")
class AutoMigration2To3Spec : AutoMigrationSpec
