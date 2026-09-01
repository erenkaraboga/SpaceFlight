package com.spaceflight.core.data.di

import com.spaceflight.core.data.connectivity.AndroidNetworkMonitor
import com.spaceflight.core.data.preferences.UserPreferencesRepositoryImpl
import com.spaceflight.core.data.repository.ArticleRepositoryImpl
import com.spaceflight.core.data.repository.FavoriteRepositoryImpl
import com.spaceflight.core.domain.connectivity.NetworkMonitor
import com.spaceflight.core.domain.repository.ArticleRepository
import com.spaceflight.core.domain.repository.FavoriteRepository
import com.spaceflight.core.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindArticleRepository(impl: ArticleRepositoryImpl): ArticleRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(impl: AndroidNetworkMonitor): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}
