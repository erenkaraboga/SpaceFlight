package com.spaceflight.core.common.error

import com.spaceflight.core.common.R
import com.spaceflight.core.common.text.UiText
import com.spaceflight.core.model.AppError
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Translates the exception zoo of OkHttp/Retrofit/kotlinx.serialization into the small set of
 * failures the UI knows how to talk about.
 */
fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is UnknownHostException, is ConnectException -> AppError.NoConnection()
    is SocketTimeoutException -> AppError.Timeout()
    is HttpException -> AppError.Server(code())
    is SerializationException -> AppError.Serialization()
    is IOException -> AppError.NoConnection()
    else -> AppError.Unknown(this)
}


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
