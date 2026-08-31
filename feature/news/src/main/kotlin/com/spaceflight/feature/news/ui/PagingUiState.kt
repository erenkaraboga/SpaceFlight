package com.spaceflight.feature.news.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.spaceflight.core.domain.model.AppError
import com.spaceflight.feature.news.R

fun LazyPagingItems<*>.isInitialLoad(): Boolean =
    loadState.refresh is LoadState.Loading && itemCount == 0

fun LazyPagingItems<*>.isInitialFailure(): Boolean =
    loadState.refresh is LoadState.Error && itemCount == 0

fun LazyPagingItems<*>.isEmptyResult(): Boolean =
    loadState.refresh is LoadState.NotLoading &&
        loadState.append.endOfPaginationReached &&
        itemCount == 0

fun LazyPagingItems<*>.refreshError(): Throwable? =
    (loadState.refresh as? LoadState.Error)?.error

@Composable
fun LazyPagingItems<*>.refreshErrorMessage(): String =
    stringResource(refreshError().messageResId())

@StringRes
fun Throwable?.messageResId(): Int = when (this) {
    is AppError.NoConnection -> R.string.error_no_connection
    is AppError.Timeout -> R.string.error_timeout
    is AppError.Server -> R.string.error_server
    is AppError.Serialization -> R.string.error_parsing
    else -> R.string.error_unknown
}
