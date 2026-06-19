package com.example.favorite

data class FavoriteItem(
    val releaseId: String,
    val artistTitle: String,
    val releaseTitle: String,
    val country: String,
    val genres: List<String>,
    val thumb: String,
    val coverImage: String?,
    val communityHave: Int,
    val communityWant: Int,
    val addedAt: Long,
)