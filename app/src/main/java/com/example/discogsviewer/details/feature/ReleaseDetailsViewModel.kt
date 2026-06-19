package com.example.discogsviewer.details.feature

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discogsviewer.R
import com.example.discogsviewer.details.domain.ConsumeReleaseDetailsUseCase
import com.example.favorite.FavoriteItem
import com.example.favorite.ToggleFavoriteUseCase
import com.example.releases.ReleasesLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReleaseDetailsViewModel @Inject constructor(
    private val consumeReleaseDetailsUseCase: ConsumeReleaseDetailsUseCase,
    private val releaseDetailsStateFactory: ReleaseDetailsStateFactory,
    private val savedStateHandle: SavedStateHandle,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val releasesLocalDataSource: ReleasesLocalDataSource,
) : ViewModel() {

    private val releaseId: String =
        checkNotNull(savedStateHandle["releaseId"]) { "releaseId must not be null" }

    private val _state = MutableStateFlow(ReleaseDetailsScreenState())
    val state: StateFlow<ReleaseDetailsScreenState> = _state.asStateFlow()

    init {
        requestProducts()
    }

    private fun requestProducts() {
        consumeReleaseDetailsUseCase(releaseId)
            .map { releaseDetails -> releaseDetailsStateFactory.create(releaseDetails) }
            .flowOn(Dispatchers.IO)
            .onStart {
                _state.update { screenState -> screenState.copy(isLoading = true) }
            }
            .onEach { detailsState ->
                _state.update { screenState ->
                    screenState.copy(
                        isLoading = false,
                        detailsState = detailsState,
                    )
                }
            }
            .catch {
                _state.update { screenState ->
                    screenState.copy(
                        hasError = true,
                        errorProvider = { context -> context.getString(R.string.error_while_loading_data) }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun errorHasShown() {
        _state.update { screenState -> screenState.copy(hasError = false) }
    }

    fun retry() {
        _state.update { screenState -> screenState.copy(hasError = false, isLoading = true) }
        requestProducts()
    }

    fun onToggleFavorite(releaseId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val details = state.value.detailsState
            val oldFavoriteState = details.isFavorite
            val newFavoriteState = isFavorite
            _state.update { screenState ->
                screenState.copy(
                    detailsState = screenState.detailsState.copy(isFavorite = newFavoriteState)
                )
            }
            val thumb = try {
                releasesLocalDataSource.getThumb(details.id)
            } catch (_: Exception) {
                null
            }
            val item = FavoriteItem(
                releaseId = releaseId,
                artistTitle = details.artistTitle,
                releaseTitle = details.releaseTitle,
                country = details.country,
                genres = details.genres,
                thumb = thumb ?: "",
                coverImage = details.coverImage,
                communityHave = details.have,
                communityWant = details.want,
                addedAt = System.currentTimeMillis(),
            )
            try {
                toggleFavoriteUseCase(item, newFavoriteState)
            } catch (e: Exception) {
                _state.update { screenState ->
                    screenState.copy(
                        detailsState = screenState.detailsState.copy(isFavorite = oldFavoriteState)
                    )
                }
            }
        }
    }
}
