package com.spaceflight.feature.news.logic

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

/** True while the very first page is still loading and there is nothing to show yet. */
fun LazyPagingItems<*>.isInitialLoad(): Boolean =
    itemCount == 0 && loadState.refresh is LoadState.Loading

fun LazyPagingItems<*>.refreshError(): Throwable? =
    (loadState.refresh as? LoadState.Error)?.error

fun LazyPagingItems<*>.appendError(): Throwable? =
    (loadState.append as? LoadState.Error)?.error
