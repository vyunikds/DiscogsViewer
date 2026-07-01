package com.example.core.basepresentation.ui

data class ReleaseCardState(
    val id: String,
    val artistTitle: String,
    val releaseTitle: String,
    val country: String,
    val genre: List<String>,
    val thumb: String,
    val coverImage: String = "",
    val isFavorite: Boolean,
)
