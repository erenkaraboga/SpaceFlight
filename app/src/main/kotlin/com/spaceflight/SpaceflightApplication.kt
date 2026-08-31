package com.spaceflight

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpaceflightApplication : Application(), SingletonImageLoader.Factory {

    /**
     * Coil's network fetcher is registered explicitly rather than left to service discovery, and
     * every image cross-fades in so it replaces its shimmer placeholder instead of popping.
     */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory()) }
            .crossfade(true)
            .build()
}
