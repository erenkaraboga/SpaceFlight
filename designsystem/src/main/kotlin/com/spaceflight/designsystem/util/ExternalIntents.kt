package com.spaceflight.designsystem.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/** Opens [url] in a Custom Tab, falling back to the default browser. */
fun Context.openUrlInCustomTab(url: String, toolbarColor: Int): Boolean {
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

fun Context.shareText(title: String, body: String, chooserTitle: String): Boolean {
    if (body.isBlank()) return false

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    return try {
        startActivity(Intent.createChooser(intent, chooserTitle))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}
