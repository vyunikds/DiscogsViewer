package com.example.discogsviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.discogsviewer.navigation.MainNavigation
import com.example.discogsviewer.presentation.theme.DiscogsViewerTheme
import com.example.settings.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

sealed class ScreenRoute(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    object TopReleases : ScreenRoute("releases_screen", R.string.nav_top_releases, Icons.Default.Star)
    object Details : ScreenRoute("details_screen/{releaseId}", R.string.nav_details, Icons.Default.Settings)
    object Favorites : ScreenRoute("favorites", R.string.nav_favorites, Icons.Default.Favorite)
    object Settings : ScreenRoute("settings", R.string.nav_settings, Icons.Default.Settings)
    object Search : ScreenRoute("search", R.string.nav_search, Icons.Default.Search)

}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by settingsRepository.consumeThemeMode()
                .collectAsState(initial = 0)
            val systemInDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                1 -> false
                2 -> true
                else -> systemInDarkTheme
            }
            DiscogsViewerTheme(darkTheme = darkTheme) {
                MainNavigation()
            }
        }
    }
}
