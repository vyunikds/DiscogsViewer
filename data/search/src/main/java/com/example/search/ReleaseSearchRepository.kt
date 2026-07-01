package com.example.search

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@OptIn(InternalSerializationApi::class)
@Singleton
class ReleaseSearchRepository @Inject constructor(
    private val releaseSearchRemoteDataSource: ReleaseSearchRemoteDataSource,
    @Named("ioDispatcher") private val dispatcher: CoroutineDispatcher,
) {
    fun consumeReleaseSearch(title: String, page: Int): Flow<List<ReleaseSearchResult>> = flow {
        val response = releaseSearchRemoteDataSource.searchRelease(title, page)
        val results = response.results.map { dto ->
            val artistTitle = dto.title.substringBefore("-").trimEnd()
            val releaseTitle = dto.title.substringAfter("-").trimStart()
            ReleaseSearchResult(
                id = dto.id.toString(),
                artistTitle = artistTitle,
                releaseTitle = releaseTitle,
                country = dto.country,
                genre = dto.genre,
                thumb = dto.thumb,
                hasNextPage = response.pagination.urls.next != null,
            )
        }
        emit(results)
    }.flowOn(dispatcher)
}
