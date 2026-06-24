package com.example.favorite

interface ToggleFavoriteUseCase {
    suspend operator fun invoke(releaseId: String, addedAt: Long, isFavorite: Boolean)
}
