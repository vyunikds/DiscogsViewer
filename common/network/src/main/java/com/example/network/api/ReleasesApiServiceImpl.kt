package com.example.network.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.network.BuildConfig.AUTH_TOKEN
import com.example.network.dto.ReleaseDetailsDto
import com.example.network.dto.ReleaseResultDto
import com.example.network.dto.SearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.InternalSerializationApi

class ReleasesApiServiceImpl(private val client: HttpClient) : ReleasesApiService {
    @OptIn(InternalSerializationApi::class)
    override suspend fun getReleases(): List<ReleaseResultDto> {
        return try {
            val url =
                "$BASE_URL/database/search?sort=have%2Cdesc&type=release&token=$AUTH_TOKEN&page=1&per_page=10"
//                "$BASE_URL/database/search?sort=have%2Cdesc&type=release&page=1&per_page=10"
            val response = client.get(url)
            val body: SearchResponse = response.body()
            body.results
        } catch (e: Exception) {
            Log.e("UserApiService", "Error fetching releases: ${e.message}", e)
            throw ReleasesApiFetchException(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(InternalSerializationApi::class)
    override suspend fun searchReleases(title: String, page: Int): SearchResponse {
        return try {
            val encodedTitle = java.net.URLEncoder.encode(title, Charsets.UTF_8)
            val url =
                "$BASE_URL/database/search?q=$encodedTitle&type=release&token=$AUTH_TOKEN&page=$page&per_page=20"
            val response = client.get(url)
            response.body()
        } catch (e: Exception) {
            Log.e("UserApiService", "Error searching releases: ${e.message}", e)
            throw ReleasesApiFetchException(e)
        }
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun getReleaseById(releaseId: Int): ReleaseDetailsDto {
        return try {
            val url = "$BASE_URL/releases/$releaseId?token=$AUTH_TOKEN"
//            val url = "$BASE_URL/releases/$releaseId"
            val response = client.get(url)
            response.body()
        } catch (e: Exception) {
            Log.e("UserApiService", "Error fetching release details: ${e.message}", e)
            throw ReleasesApiFetchException(e)
        }
    }

}

private const val BASE_URL = "https://api.discogs.com"
