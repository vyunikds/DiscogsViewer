package com.example.discogsviewer.releases.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discogsviewer.R
import com.example.favorite.ToggleFavoriteUseCase
import com.example.releases.ReleasesRepository
import com.example.discogsviewer.releases.domain.ConsumeReleasesUseCase
import com.example.discogsviewer.releases.domain.ReleaseWithFavorite
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
import javax.inject.Inject

@HiltViewModel
class ReleasesViewModel @Inject constructor(
    private val consumeReleasesUseCase: ConsumeReleasesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val releasesStateFactory: ReleasesStateFactory,
    private val releasesRepository: ReleasesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ReleasesScreenState>(ReleasesScreenState())
    val state: StateFlow<ReleasesScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            releasesRepository.fetchAndSaveIfNeeded()
        }
        collectData()
    }

    private fun collectData() {
        consumeReleasesUseCase().map { releases: List<ReleaseWithFavorite> ->
            releases.map { release -> releasesStateFactory.create(release) }
        }
            .onEach { releaseListState ->
                _state.update { screenState ->
                    screenState.copy(
                        isInitialized = true,
                        releasesListState = releaseListState
                    )
                }
            }
            .catch {
                _state.update { screenState ->
                    screenState.copy(
                        isInitialized = true,
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
            } catch (_: Exception) {
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
            toggleFavoriteUseCase(releaseId, System.currentTimeMillis(), isFavorite)
        }
    }
}
