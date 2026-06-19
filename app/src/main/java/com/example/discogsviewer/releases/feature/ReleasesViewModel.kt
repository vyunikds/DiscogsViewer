package com.example.discogsviewer.releases.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discogsviewer.R
import com.example.favorite.FavoriteItem
import com.example.favorite.ToggleFavoriteUseCase
import com.example.releases.ReleasesRepository
import com.example.discogsviewer.releases.domain.ConsumeReleasesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

@HiltViewModel
class ReleasesViewModel @Inject constructor(
    private val consumeReleasesUseCase: ConsumeReleasesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val releasesStateFactory: ReleasesStateFactory,
    private val releasesRepository: ReleasesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ReleasesScreenState())
    val state: StateFlow<ReleasesScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            releasesRepository.fetchAndSaveIfNeeded()
        }
        collectData()
    }

    @OptIn(InternalSerializationApi::class)
    private fun collectData() {
        consumeReleasesUseCase().map { releases ->
            releases.map { release -> releasesStateFactory.create(release) }
        }
            .onEach { releaseListState ->
                _state.update { screenState ->
                    screenState.copy(releasesListState = releaseListState)
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

    fun refresh() {
        viewModelScope.launch {
            _state.update { screenState -> screenState.copy(isLoading = true) }
            try {
                releasesRepository.fetchAndSave()
            } catch (e: Exception) {
                _state.update { screenState ->
                    screenState.copy(
                        hasError = true,
                        errorProvider = { context -> context.getString(R.string.error_while_loading_data) }
                    )
                }
                return@launch
            } finally {
                _state.update { screenState -> screenState.copy(isLoading = false) }
            }
        }
    }

    fun errorHasShown() {
        _state.update { screenState -> screenState.copy(hasError = false) }
    }

    fun onToggleFavorite(releaseId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val releaseState = state.value.releasesListState.find { it.id.toString() == releaseId } ?: return@launch
            val item = FavoriteItem(
                releaseId = releaseId,
                artistTitle = releaseState.artistTitle,
                releaseTitle = releaseState.releaseTitle,
                country = releaseState.country,
                genres = releaseState.genre,
                thumb = releaseState.thumb,
                coverImage = "",
                communityHave = 0,
                communityWant = 0,
                addedAt = System.currentTimeMillis(),
            )
            toggleFavoriteUseCase(item, isFavorite)
        }
    }
}