package com.example.network.api

import android.util.Log
import com.example.network.BuildConfig.AUTH_TOKEN
import com.example.network.dto.ReleaseDetailsDto
import com.example.network.dto.ReleaseResultDto
import com.example.network.dto.SearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class ReleasesApiServiceImpl(
    private val client: HttpClient,
) : ReleasesApiService {
    override suspend fun getReleases(): List<ReleaseResultDto> =
        withContext(Dispatchers.IO) {
            try {
                val encodedSort = URLEncoder.encode(SORT, "UTF-8")
                val url =
                    "$BASE_URL/database/search?sort=$encodedSort&type=$ITEM_TYPE&" +
                        "token=$AUTH_TOKEN&page=$DEFAULT_PAGE&per_page=$RELEASE_PER_PAGE"
                val response = client.get(url)
                val body: SearchResponse = response.body()
                body.results
            } catch (e: java.net.SocketException) {
                Log.e("ReleasesApiServiceImpl", "Error fetching releases: ${e.message}", e)
                throw ReleasesApiFetchException(e)
            } catch (e: java.util.concurrent.TimeoutException) {
                Log.e("ReleasesApiServiceImpl", "Error fetching releases: ${e.message}", e)
                throw ReleasesApiFetchException(e)
            } catch (e: kotlinx.serialization.SerializationException) {
                Log.e("ReleasesApiServiceImpl", "Error fetching releases: ${e.message}", e)
                throw ReleasesApiFetchException(e)
            }
        }

    override suspend fun searchReleases(
        title: String,
        page: Int,
    ): SearchResponse =
        withContext(Dispatchers.IO) {
            try {
                val encodedTitle = URLEncoder.encode(title, "UTF-8")
                val url =
                    "$BASE_URL/database/search?q=$encodedTitle&type=$ITEM_TYPE&" +
                        "token=$AUTH_TOKEN&page=$page&per_page=$SEARCH_PER_PAGE"
                val response = client.get(url)
                response.body()
            } catch (e: java.net.SocketException) {
                Log.e("ReleasesApiServiceImpl", "Error searching releases: ${e.message}", e)
                throw ReleasesApiFetchException(e)
            } catch (e: java.util.concurrent.TimeoutException) {
                Log.e("ReleasesApiServiceImpl", "Error searching releases: ${e.message}", e)
                throw ReleasesApiFetchException(e)
            } catch (e: kotlinx.serialization.SerializationException) {
                Log.e("ReleasesApiServiceImpl", "Error searching releases: ${e.message}", e)
                throw ReleasesApiFetchException(e)
            }
        }

    override suspend fun getReleaseById(releaseId: Int): ReleaseDetailsDto =
        withContext(Dispatchers.IO) {
            try {
                val url = "$BASE_URL/releases/$releaseId?token=$AUTH_TOKEN"
                val response = client.get(url)
                response.body()
            } catch (e: java.net.SocketException) {
                Log.e("ReleasesApiServiceImpl", "Error fetching release details: ${e.message}", e)
                throw ReleasesApiFetchException(e)
            } catch (e: java.util.concurrent.TimeoutException) {
                Log.e("ReleasesApiServiceImpl", "Error fetching release details: ${e.message}", e)
                throw ReleasesApiFetchException(e)
            } catch (e: kotlinx.serialization.SerializationException) {
                Log.e("ReleasesApiServiceImpl", "Error fetching release details: ${e.message}", e)
                throw ReleasesApiFetchException(e)
            }
        }
}

private const val BASE_URL = "https://api.discogs.com"
private const val ITEM_TYPE = "release"
private const val DEFAULT_PAGE = 1
private const val RELEASE_PER_PAGE = 10
private const val SEARCH_PER_PAGE = 20
private const val SORT = "have,desc"
