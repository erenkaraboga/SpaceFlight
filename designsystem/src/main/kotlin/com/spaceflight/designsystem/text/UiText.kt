package com.spaceflight.designsystem.text

import android.content.Context
import androidx.annotation.StringRes

/**
 * A string that isn't resolved yet: an Android string resource plus its format args. Lets a
 * ViewModel describe user-facing text without holding a [Context] or `@Composable` scope, so it
 * stays plain-Kotlin and unit-testable. Generic on purpose — nothing here knows about any app's
 * domain types, so this type (and the resolver below) can move to another project unchanged.
 */
sealed interface UiText {
    data class Resource(@StringRes val resId: Int, val args: List<Any> = emptyList()) : UiText
}

/** Resolves this [UiText] to a plain [String] outside composition (e.g. inside a Channel/effect collector). */
fun UiText.asString(context: Context): String = when (this) {
    is UiText.Resource -> context.getString(resId, *args.toTypedArray())
}
