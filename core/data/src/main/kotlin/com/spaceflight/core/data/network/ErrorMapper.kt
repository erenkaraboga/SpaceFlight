package com.spaceflight.core.data.network

import com.spaceflight.core.domain.model.AppError
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
