package com.example.discogsviewer.details.domain

data class ReleaseDetailsWithFavorite (
    val release: ReleaseDetails,
    val isFavorite: Boolean,
)
