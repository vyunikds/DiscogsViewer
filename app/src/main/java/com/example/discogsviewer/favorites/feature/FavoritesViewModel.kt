package com.example.discogsviewer.favorites.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discogsviewer.R
import com.example.discogsviewer.favorites.domain.FavoriteSortMode
import com.example.discogsviewer.favorites.domain.LoadFavoritesPageUseCase
import com.example.favorite.FavoriteItem
import com.example.favorite.FavoritesRepository
import com.example.favorite.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val loadFavoritesPageUseCase: LoadFavoritesPageUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val favoritesStateFactory: FavoritesStateFactory,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesScreenState())
    val state: StateFlow<FavoritesScreenState> = _state.asStateFlow()

    private var currentSortMode = FavoriteSortMode.BY_DATE
    private val pageSize = 20

    fun setSortMode(mode: FavoriteSortMode) {
        currentSortMode = mode
        _state.update { it.copy(currentSortMode = mode) }
        resetAndReload()
    }

    init {
        favoritesRepository.consumeCount()
            .onEach { count ->
                _state.update { it.copy(totalCount = count) }
            }
            .launchIn(viewModelScope)
        loadPage(0)
    }

    fun loadMore() {
        val curr = _state.value
        if (curr.isLoadingMore || !curr.hasNextPage) return
        val nextPage = curr.favorites.size / pageSize
        loadPage(nextPage)
    }

    fun onRemoveFavorite(productId: String) {
        viewModelScope.launch {
            val favoriteState = state.value.favorites.find { it.id == productId } ?: return@launch
            val item = FavoriteItem(
                releaseId = productId,
                artistTitle = favoriteState.artistTitle,
                releaseTitle = favoriteState.releaseTitle,
                country = favoriteState.country,
                genres = favoriteState.genre,
                thumb = favoriteState.thumb,
                coverImage = null,
                communityHave = 0,
                communityWant = 0,
                addedAt = 0L,
            )
            toggleFavoriteUseCase(item, false)
            resetAndReload()
        }
    }

    private fun resetAndReload() {
        _state.update {
            it.copy(
                favorites = emptyList(),
                hasNextPage = true,
                isLoading = true,
                isLoadingMore = false,
            )
        }
        loadPage(0)
    }

    private fun loadPage(requestedPage: Int) {
        val offset = requestedPage * pageSize
        if (requestedPage == 0) {
            _state.update { it.copy(isLoading = true, isLoadingMore = false) }
        } else {
            _state.update { it.copy(isLoadingMore = true) }
        }

        viewModelScope.launch {
            try {
                val releasesWithFavorite = loadFavoritesPageUseCase(currentSortMode, pageSize, offset)
                val states = favoritesStateFactory.create(releasesWithFavorite)
                val hasNext = releasesWithFavorite.size == pageSize
                _state.update {
                    it.copy(
                        favorites = if (requestedPage == 0) states else it.favorites + states,
                        isLoading = false,
                        isLoadingMore = false,
                        hasNextPage = hasNext,
                        hasError = false,
                        errorProvider = { "" },
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        hasError = true,
                        errorProvider = { context -> e.message ?: context.getString(R.string.error_unknown) }
                    )
                }
            }
        }
    }
}
