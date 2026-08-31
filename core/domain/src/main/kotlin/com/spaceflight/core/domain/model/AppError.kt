package com.spaceflight.core.domain.model

/**
 * Every failure the data layer can surface, expressed as a [Throwable] so that it can travel
 * unchanged through Paging's `LoadState.Error` as well as through regular suspend functions.
 */
sealed class AppError(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {

    class NoConnection : AppError("No internet connection")

    class Timeout : AppError("The request timed out")

    class Server(val code: Int) : AppError("Server responded with $code")

    class Serialization : AppError("The response could not be parsed")

    class Unknown(cause: Throwable?) : AppError("Unexpected error", cause)
}
