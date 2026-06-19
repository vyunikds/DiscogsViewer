package com.example.favorite

import javax.inject.Inject

class ToggleFavoriteUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : ToggleFavoriteUseCase {

    override suspend fun invoke(item: FavoriteItem, isFavorite: Boolean) {
        if (isFavorite) {
            favoritesRepository.addToFavorites(item)
        } else {
            favoritesRepository.removeFromFavorites(item.releaseId)
        }
    }
}
