package com.example.feature.search.domain

data class ReleaseSearchWithFavorite(
    val release: ReleaseSearch,
    val isFavorite: Boolean,
)
