package com.example.favorite

interface ToggleFavoriteUseCase {
    suspend operator fun invoke(item: FavoriteItem, isFavorite: Boolean)
}
