package com.spaceflight.core.data.network

import com.spaceflight.core.domain.model.AppError
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMapperTest {

    @Test
    fun `a missing DNS entry means there is no connection`() {
        assertTrue(UnknownHostException().toAppError() is AppError.NoConnection)
    }

    @Test
    fun `a refused connection means there is no connection`() {
        assertTrue(ConnectException().toAppError() is AppError.NoConnection)
    }

    @Test
    fun `a socket timeout maps to the timeout error`() {
        assertTrue(SocketTimeoutException().toAppError() is AppError.Timeout)
    }

    @Test
    fun `an http failure keeps its status code`() {
        val httpException = HttpException(
            Response.error<Unit>(503, "".toResponseBody("application/json".toMediaType()))
        )

        val error = httpException.toAppError()

        assertTrue(error is AppError.Server)
        assertEquals(503, (error as AppError.Server).code)
    }

    @Test
    fun `a malformed payload maps to the serialization error`() {
        assertTrue(SerializationException("bad json").toAppError() is AppError.Serialization)
    }

    @Test
    fun `any other IO problem is treated as a lost connection`() {
        assertTrue(IOException("socket closed").toAppError() is AppError.NoConnection)
    }

    @Test
    fun `anything unrecognised is wrapped and keeps its cause`() {
        val cause = IllegalStateException("boom")

        val error = cause.toAppError()

        assertTrue(error is AppError.Unknown)
        assertSame(cause, error.cause)
    }

    @Test
    fun `an error that is already mapped passes through untouched`() {
        val original = AppError.Timeout()

        assertSame(original, original.toAppError())
    }
}
