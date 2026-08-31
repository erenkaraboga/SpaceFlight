package com.spaceflight

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class SpaceflightApplication : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    /**
     * Coil's network fetcher is registered explicitly rather than left to service discovery, and
     * every image cross-fades in so it replaces its shimmer placeholder instead of popping.
     * Sharing the app OkHttp client also routes image traffic through the debug network logger.
     */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory(okHttpClient)) }
            .crossfade(true)
            .build()
}
