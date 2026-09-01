package com.spaceflight.core.common.error

import com.spaceflight.core.model.AppError
import com.spaceflight.designsystem.text.UiText
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorPresentationTest {

    @Test
    fun `every AppError case maps to a resource-backed message, never a blank string`() {
        val errors = listOf(
            AppError.NoConnection(),
            AppError.Timeout(),
            AppError.Server(503),
            AppError.Serialization(),
            AppError.Unknown(RuntimeException("boom")),
        )

        errors.forEach { error ->
            assertTrue(error.toUiText() is UiText.Resource)
        }
    }

    @Test
    fun `a non-AppError throwable widens to Unknown instead of being dropped`() {
        val error = RuntimeException("boom").toAppErrorOrUnknown()

        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `an AppError already typed passes through unchanged`() {
        val original = AppError.Timeout()

        assertTrue(original.toAppErrorOrUnknown() === original)
    }
}
