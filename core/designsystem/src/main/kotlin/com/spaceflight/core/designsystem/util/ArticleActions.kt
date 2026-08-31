package com.spaceflight.core.designsystem.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/**
 * SNAPI hands out a publisher URL rather than article text, so "read more" opens a Custom Tab: it
 * keeps the app's colours and back gesture instead of handing the reader to another app entirely.
 *
 * Returns `false` when the device has nothing that can show a web page, letting the caller say so.
 */
fun Context.openArticleInCustomTab(url: String, toolbarColor: Int): Boolean {
    if (url.isBlank()) return false

    return try {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(toolbarColor)
                    .build()
            )
            .build()
            .launchUrl(this, url.toUri())
        true
    } catch (_: ActivityNotFoundException) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}

fun Context.shareArticle(title: String, url: String, chooserTitle: String): Boolean {
    if (url.isBlank()) return false

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title\n\n$url")
    }

    return try {
        startActivity(Intent.createChooser(intent, chooserTitle))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}
