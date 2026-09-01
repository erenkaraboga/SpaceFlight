package com.spaceflight.core.common.error

import com.spaceflight.core.model.AppError
import com.spaceflight.core.common.R
import com.spaceflight.designsystem.text.UiText

/**
 * The single place that turns an [AppError] into copy a user can read. Every screen in the app —
 * paging refresh errors, a failed favorite toggle, a failed detail refresh — goes through this
 * one function, so the wording for "no connection" (or any other case) can never drift between
 * screens the way it had before this file existed.
 */
fun AppError.toUiText(): UiText = when (this) {
    is AppError.NoConnection -> UiText.Resource(R.string.error_no_connection)
    is AppError.Timeout -> UiText.Resource(R.string.error_timeout)
    is AppError.Server -> UiText.Resource(R.string.error_server)
    is AppError.Serialization -> UiText.Resource(R.string.error_parsing)
    is AppError.Unknown -> UiText.Resource(R.string.error_unknown)
}

/** Widens any [Throwable] to the [AppError] currency this module deals in. */
fun Throwable?.toAppErrorOrUnknown(): AppError = this as? AppError ?: AppError.Unknown(this)
