package com.example.search

import com.example.network.dto.ReleaseResultDto
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
    fun consumeReleaseSearch(title: String): Flow<List<ReleaseResultDto>> = flow {
        val releases = releaseSearchRemoteDataSource.searchRelease(title)
        emit(releases)
    }.flowOn(dispatcher)
}
