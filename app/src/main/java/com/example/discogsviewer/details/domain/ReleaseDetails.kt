package com.example.discogsviewer.details.domain

data class ReleaseDetails(
    val id: Int,
    val releaseTitle: String,
    val artistTitle: String,
    val country: String,
    val genres: List<String>,
    val coverImage: String,
    val isFavorite: Boolean,
    val want: Int,
    val have: Int,
)
