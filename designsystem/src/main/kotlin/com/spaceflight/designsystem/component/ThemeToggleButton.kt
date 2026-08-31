package com.spaceflight.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spaceflight.designsystem.R
import com.spaceflight.designsystem.theme.LocalIsDarkTheme
import com.spaceflight.designsystem.theme.LocalToggleTheme

@Composable
fun ThemeToggleButton(modifier: Modifier = Modifier) {
    val onToggle = LocalToggleTheme.current ?: return
    val isDark = LocalIsDarkTheme.current

    IconButton(
        onClick = onToggle,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Icon(
            imageVector = if (isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
            contentDescription = stringResource(
                if (isDark) R.string.ds_use_light_theme else R.string.ds_use_dark_theme,
            ),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp),
        )
    }
}
