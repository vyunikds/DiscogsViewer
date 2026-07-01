package com.example.core.basepresentation

object Deeplink {
    fun openReleaseDetails(releaseId: String): String = ScreenRouter.detailsRoute(releaseId)
}
