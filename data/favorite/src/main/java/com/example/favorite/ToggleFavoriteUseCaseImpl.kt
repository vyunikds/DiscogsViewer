package com.example.favorite

import javax.inject.Inject

class ToggleFavoriteUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : ToggleFavoriteUseCase {

    override suspend fun invoke(releaseId: String, addedAt: Long, isFavorite: Boolean) {
        if (isFavorite) {
            favoritesRepository.addToFavorites(FavoriteItem(releaseId, addedAt))
        } else {
            favoritesRepository.removeFromFavorites(releaseId)
        }
    }
}
