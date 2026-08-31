package com.spaceflight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.spaceflight.core.designsystem.theme.SpaceflightTheme
import com.spaceflight.navigation.SpaceflightApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            SpaceflightTheme {
                SpaceflightApp()
            }
        }
    }
}
