package com.example.discogsviewer.search.domain

data class ReleaseSearchWithFavorite(
    val release: ReleaseSearch,
    val isFavorite: Boolean,
)
