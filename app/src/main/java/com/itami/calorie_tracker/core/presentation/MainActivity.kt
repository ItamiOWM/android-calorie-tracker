package com.itami.calorie_tracker.core.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.itami.calorie_tracker.core.presentation.navigation.Graph
import com.itami.calorie_tracker.core.presentation.navigation.rememberNavigationState
import com.itami.calorie_tracker.core.presentation.theme.CalorieTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            mainViewModel.showSplash
        }
        setContent {
            val theme = mainViewModel.theme
            val startRoute = when {
                mainViewModel.isAuthenticated -> Graph.Diary.route
                mainViewModel.showOnboarding -> Graph.Onboarding.route
                else -> Graph.Auth.route
            }
            CalorieTrackerTheme(theme = theme) {
                val navState = rememberNavigationState()
                MainScreen(
                    startRoute = startRoute,
                    navState = navState,
                )
            }
        }
    }
}