package com.example.core.basepresentation

object ScreenRouter {
    const val RELEASES_SCREEN = "releases_screen"
    const val DETAILS_SCREEN = "details_screen/{releaseId}"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val SEARCH = "search"

    fun detailsRoute(releaseId: String): String = "details_screen/$releaseId"
}
