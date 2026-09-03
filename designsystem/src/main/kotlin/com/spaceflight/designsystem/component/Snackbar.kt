package com.spaceflight.designsystem.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

/**
 * Shows [message] right away, dismissing whatever snackbar is currently on screen first instead of
 * queuing behind it.
 *
 * [SnackbarHostState.showSnackbar] on its own stacks concurrent calls one after another: each
 * caller suspends until the previous snackbar finishes its full duration or is dismissed. That is
 * fine when there is a single message source, but every screen here feeds the same host from
 * several independent places (a favorite-toggle confirmation, a refresh error, an append error, a
 * share/browser failure, ...). Without this, a stale message from a few seconds ago can still be
 * finishing its run while a newer, more relevant one waits in line behind it. Replacing instead of
 * queuing keeps only the latest message visible, which is what a single-line, non-critical status
 * channel like a snackbar should do.
 */
suspend fun SnackbarHostState.showLatestSnackbar(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
): SnackbarResult {
    currentSnackbarData?.dismiss()
    return showSnackbar(
        message = message,
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = duration,
    )
}
