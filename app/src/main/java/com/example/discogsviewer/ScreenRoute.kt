package com.example.discogsviewer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.core.basepresentation.ScreenRouter.DETAILS_SCREEN
import com.example.core.basepresentation.ScreenRouter.FAVORITES
import com.example.core.basepresentation.ScreenRouter.RELEASES_SCREEN
import com.example.core.basepresentation.ScreenRouter.SEARCH
import com.example.core.basepresentation.ScreenRouter.SETTINGS

sealed class ScreenRoute(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector
) {
    object TopReleases : ScreenRoute(
        route = RELEASES_SCREEN,
        titleRes = R.string.nav_top_releases,
        icon = Icons.Default.Star
    )

    object Details : ScreenRoute(
        route = DETAILS_SCREEN,
        titleRes = R.string.nav_details,
        icon = Icons.Outlined.Info
    )

    object Favorites : ScreenRoute(
        route = FAVORITES,
        titleRes = R.string.nav_favorites,
        icon = Icons.Default.Favorite
    )

    object Settings : ScreenRoute(
        route = SETTINGS,
        titleRes = R.string.nav_settings,
        icon = Icons.Default.Settings
    )

    object Search : ScreenRoute(
        route = SEARCH,
        titleRes = R.string.nav_search,
        icon = Icons.Default.Search
    )
}
