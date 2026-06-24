package com.example.favorite

import com.example.database.dbo.FullReleaseDbo

data class FavoriteItem(
    val releaseId: String,
    val addedAt: Long,
)

data class FavoriteReleaseItem(
    val releaseId: String,
    val fullRelease: FullReleaseDbo,
)
