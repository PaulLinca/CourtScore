package com.linca.courtscore.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.linca.courtscore.data.LocaleHelper
import com.linca.courtscore.data.PreferencesManager
import com.linca.courtscore.presentation.theme.ColorScheme
import com.linca.courtscore.presentation.theme.ColorSchemes
import com.linca.courtscore.presentation.theme.CourtScoreTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager

    override fun attachBaseContext(newBase: Context) {
        preferencesManager = PreferencesManager(newBase)
        val languageCode = runBlocking {
            preferencesManager.languageFlow.first()
        }
        val context = if (languageCode != "system") {
            LocaleHelper.setLocale(newBase, languageCode)
        } else {
            newBase
        }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            val colorSchemeName by preferencesManager.colorSchemeFlow.collectAsState(initial = ColorSchemes.SunsetOcean.name)
            val colorScheme = ColorSchemes.findByName(colorSchemeName)

            WearApp(
                colorScheme = colorScheme,
                preferencesManager = preferencesManager
            )
        }
    }
}

@Composable
fun WearApp(
    colorScheme: ColorScheme,
    preferencesManager: PreferencesManager
) {
    CourtScoreTheme(colorScheme = colorScheme) {
        val navController = rememberSwipeDismissableNavController()

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "start"
        ) {
            composable("start") {
                MainScreen(
                    onNewMatch = {
                        navController.navigate("newMatch")
                    },
                    onSettingsClick = {
                        navController.navigate("settings")
                    }
                )
            }

            composable(route = "newMatch") {
                MatchScreen(
                    preferencesManager = preferencesManager,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    preferencesManager = preferencesManager,
                    onNavigateBack = {
                        navController.popBackStack()
                        true
                    }
                )
            }
        }
    }
}
