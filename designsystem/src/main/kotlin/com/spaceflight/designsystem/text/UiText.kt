package com.spaceflight.designsystem.text

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * A string that isn't resolved yet: either a literal (already-final) value, or an Android string
 * resource plus its format args. Lets a ViewModel describe user-facing text without holding a
 * [Context] or `@Composable` scope, so it stays plain-Kotlin and unit-testable. Generic on
 * purpose — nothing here knows about any app's domain types, so this type (and the resolvers
 * below) can move to another project unchanged.
 */
sealed interface UiText {

    data class Literal(val value: String) : UiText

    data class Resource(@StringRes val resId: Int, val args: List<Any> = emptyList()) : UiText
}

/** Resolves this [UiText] to a plain [String] from composable code. */
@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Literal -> value
    is UiText.Resource -> stringResource(resId, *args.toTypedArray())
}

/** Resolves this [UiText] to a plain [String] outside composition (e.g. inside a Channel/effect collector). */
fun UiText.asString(context: Context): String = when (this) {
    is UiText.Literal -> value
    is UiText.Resource -> context.getString(resId, *args.toTypedArray())
}
