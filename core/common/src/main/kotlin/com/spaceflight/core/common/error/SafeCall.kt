package com.spaceflight.core.common.error

import kotlinx.coroutines.CancellationException

/**
 * Runs [block] and turns any exception it throws into a [Result] whose failure is always an
 * [com.spaceflight.core.model.AppError]. Every repository function that can fail should
 * route through this instead of hand-rolling its own try/catch/toAppError() skeleton, so a new
 * repository method can't accidentally forget to normalize its exceptions.
 */
suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (error: Exception) {
    Result.failure(error.toAppError())
}
