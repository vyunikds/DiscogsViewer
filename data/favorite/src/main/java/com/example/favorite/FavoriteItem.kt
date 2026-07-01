package com.example.favorite

data class FavoriteItem(
    val releaseId: String,
    val addedAt: Long,
)

data class FavoriteReleaseItem(
    val releaseId: String,
    val artistTitle: String,
    val releaseTitle: String,
    val country: String,
    val genres: List<String>,
    val thumb: String,
    val coverImage: String,
)
