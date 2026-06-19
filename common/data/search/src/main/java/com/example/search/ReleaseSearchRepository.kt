package com.example.search

import com.example.network.dto.SearchResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ReleaseSearchRepository @Inject constructor(
    private val releaseSearchRemoteDataSource: ReleaseSearchRemoteDataSource,
    @Named("ioDispatcher") private val dispatcher: CoroutineDispatcher,
) {
    @OptIn(InternalSerializationApi::class)
    fun consumeReleaseSearch(title: String, page: Int): Flow<SearchResponse> = flow {
        val response = releaseSearchRemoteDataSource.searchRelease(title, page)
        emit(response)
    }.flowOn(dispatcher)
}
