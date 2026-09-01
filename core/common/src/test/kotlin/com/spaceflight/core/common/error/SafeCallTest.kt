package com.spaceflight.core.common.error

import com.spaceflight.core.model.AppError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.UnknownHostException

class SafeCallTest {

    @Test
    fun `a successful block is wrapped as a success`() = runTest {
        val result = safeCall { 42 }

        assertEquals(42, result.getOrNull())
    }

    @Test
    fun `a thrown exception is normalized to an AppError failure, never left raw`() = runTest {
        val result = safeCall<Unit> { throw UnknownHostException() }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.NoConnection)
    }

    @Test(expected = CancellationException::class)
    fun `cancellation is rethrown rather than wrapped into a failure`() = runTest {
        safeCall<Unit> { throw CancellationException("cancelled") }
    }
}
