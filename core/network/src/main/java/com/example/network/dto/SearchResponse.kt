package com.example.network.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class SearchResponse(
    val pagination: Pagination,
    val results: List<ReleaseResultDto>
)

@InternalSerializationApi
@Serializable
data class Pagination(
    val page: Int,
    val pages: Int,
    @SerialName("per_page") val perPage: Int,
    val items: Int,
    val urls: PaginationUrls
)

@InternalSerializationApi
@Serializable
data class PaginationUrls(
    val last: String? = null,
    val next: String? = null,
)

@InternalSerializationApi
@Serializable
data class ReleaseResultDto(
    val title: String,
    val country: String,
    val id: Int,
    val genre: List<String>,
    val thumb: String,
    @SerialName("cover_image") val coverImage: String,
    val community: Community,
)

@InternalSerializationApi
@Serializable
data class Community(
    val want: Int,
    val have: Int
)