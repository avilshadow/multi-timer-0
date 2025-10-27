package com.yogatimer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.yogatimer.app.presentation.navigation.AppNavGraph
import com.yogatimer.app.presentation.theme.YogaTimerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for Yoga Timer app.
 *
 * Uses Jetpack Compose for UI and Navigation Compose for navigation.
 * Hilt provides dependency injection throughout the app.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            YogaTimerTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}
