package com.spaceflight.core.data.di

import android.content.Context
import androidx.room.Room
import com.spaceflight.core.data.database.SpaceflightDatabase
import com.spaceflight.core.data.database.dao.ArticleDao
import com.spaceflight.core.data.database.dao.FavoriteArticleDao
import com.spaceflight.core.data.database.dao.RemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SpaceflightDatabase =
        Room.databaseBuilder(context, SpaceflightDatabase::class.java, SpaceflightDatabase.NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideArticleDao(database: SpaceflightDatabase): ArticleDao = database.articleDao()

    @Provides
    fun provideFavoriteArticleDao(database: SpaceflightDatabase): FavoriteArticleDao =
        database.favoriteArticleDao()

    @Provides
    fun provideRemoteKeyDao(database: SpaceflightDatabase): RemoteKeyDao = database.remoteKeyDao()
}
