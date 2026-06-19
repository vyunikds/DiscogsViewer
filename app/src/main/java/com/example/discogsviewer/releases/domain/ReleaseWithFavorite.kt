package com.example.discogsviewer.releases.domain

data class ReleaseWithFavorite(
    val release: Release,
    val isFavorite: Boolean,
)
