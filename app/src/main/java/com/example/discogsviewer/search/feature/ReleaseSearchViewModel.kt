package com.example.discogsviewer.search.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discogsviewer.R
import com.example.discogsviewer.search.domain.ConsumeReleasesSearchUseCase
import com.example.discogsviewer.search.domain.SearchResultPage
import com.example.favorite.FavoriteItem
import com.example.favorite.ToggleFavoriteUseCase
import com.example.settings.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

@HiltViewModel
class ReleaseSearchViewModel @Inject constructor(
    private val consumeReleasesSearchUseCase: ConsumeReleasesSearchUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val releasesSearchStateFactory: ReleasesSearchStateFactory,
    private val searchHistoryRepository: SearchHistoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ReleasesSearchScreenState())
    val state: StateFlow<ReleasesSearchScreenState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var requestJob: Job? = null

    init {
        searchHistoryRepository.consumeHistory()
            .onEach { history ->
                _state.update { screenState ->
                    screenState.copy(searchHistory = history.takeLast(5).reversed())
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(InternalSerializationApi::class)
    fun onSearchQueryChanged(query: String) {
        _state.update { screenState ->
            screenState.copy(searchQuery = query)
        }

        if (query.isBlank()) {
            _state.update { screenState ->
                screenState.copy(
                    isLoading = false,
                    releasesSearchListState = emptyList(),
                )
            }
            searchJob?.cancel()
            requestJob?.cancel()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            requestReleases(query)
        }
    }

    fun confirmQuery(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.addQuery(query)
        }
    }

    fun refresh() {
        val query = _state.value.searchQuery
        if (query.isNotBlank()) {
            requestReleases(query)
        }
    }

    private var currentPage = 1

    @OptIn(InternalSerializationApi::class)
    private fun requestReleases(searchQuery: String) {
        requestJob?.cancel()
        currentPage = 1

        requestJob = consumeReleasesSearchUseCase(searchQuery, 1).onStart {
            _state.update { screenState ->
                screenState.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    hasNextPage = true,
                )
            }
        }
            .onEach { page ->
                _state.update { screenState ->
                    screenState.copy(
                        isLoading = false,
                        releasesSearchListState = mapSearchResultPage(page),
                        hasNextPage = page.hasNextPage,
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

    fun loadMore() {
        val curr = _state.value
        if (curr.isLoadingMore || !curr.hasNextPage) return

        _state.update { it.copy(isLoadingMore = true) }
        currentPage++

        viewModelScope.launch {
            try {
                consumeReleasesSearchUseCase(_state.value.searchQuery, currentPage)
                    .collect { page ->
                        val newItems = mapSearchResultPage(page)
                        _state.update { screenState ->
                            val existingIds = screenState.releasesSearchListState.mapTo(mutableSetOf()) { it.id }
                            val uniqueNew = newItems.filter { it.id !in existingIds }
                            screenState.copy(
                                isLoadingMore = false,
                                releasesSearchListState = screenState.releasesSearchListState + uniqueNew,
                                hasNextPage = page.hasNextPage,
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingMore = false,
                        hasError = true,
                        errorProvider = { it.getString(R.string.error_while_loading_data) }
                    )
                }
            }
        }
    }

    private fun mapSearchResultPage(page: SearchResultPage): List<ReleaseSearchState> {
        return page.releases.map { releasesSearchStateFactory.create(it) }
    }


    fun removeHistoryItem(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.removeQuery(query)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearHistory()
        }
    }

    fun errorHasShown() {
        _state.update { screenState -> screenState.copy(hasError = false) }
    }

    fun onToggleFavorite(releaseId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val releaseState = state.value.releasesSearchListState.find { it.id == releaseId } ?: return@launch
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
