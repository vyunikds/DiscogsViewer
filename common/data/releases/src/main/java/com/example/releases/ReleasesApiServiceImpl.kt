package com.example.releases

import android.util.Log
import com.example.network.api.ReleasesApiFetchException
import com.example.network.api.ReleasesApiService
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
                "$BASE_URL/database/search?sort=have%2Cdesc&type=release&token=tXqTWxdQCzVvnukJWGiGbhglPYVzkPImWPWEAeXX&page=1&per_page=10"
            val response = client.get(url)
            val body: SearchResponse = response.body()
            body.results
        } catch (e: Exception) {
            Log.e("UserApiService", "Error fetching releases: ${e.message}", e)
            throw ReleasesApiFetchException(e)
        }
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun searchReleases(title: String): List<ReleaseResultDto> {
        return try {
            val url =
                "$BASE_URL/database/search?release_title=$title&type=release&token=tXqTWxdQCzVvnukJWGiGbhglPYVzkPImWPWEAeXX&page=1&per_page=10"
            val response = client.get(url)
            val body: SearchResponse = response.body()
            body.results
        } catch (e: Exception) {
            Log.e("UserApiService", "Error fetching releases: ${e.message}", e)
            throw ReleasesApiSearchException(e)
        }
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun getReleaseById(releaseId: Int): ReleaseDetailsDto {
        return try {
            val url = "$BASE_URL/releases/$releaseId?token=tXqTWxdQCzVvnukJWGiGbhglPYVzkPImWPWEAeXX"
            val response = client.get(url)
            response.body()
        } catch (e: Exception) {
            Log.e("UserApiService", "Error fetching release details: ${e.message}", e)
            throw ReleasesApiFetchException(e)
        }
    }
}

private const val BASE_URL = "https://api.discogs.com"
