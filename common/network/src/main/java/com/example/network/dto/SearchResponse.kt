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
//    val format: List<String>,
//    val style: List<String>,
//    val label: List<String>,
//    val type: String,
//    val barcode: List<String>,
//    @SerialName("user_data") val userData: UserData,
//    @SerialName("master_id") val masterId: Int? = null,
//    @SerialName("master_url") val masterUrl: String? = null,
//    val uri: String,
//    val catno: String,
    val thumb: String,
    @SerialName("cover_image") val coverImage: String,
//    @SerialName("resource_url") val resourceUrl: String,
    val community: Community,
//    @SerialName("format_quantity") val formatQuantity: Int,
//    val formats: List<FormatDetail>
)

@InternalSerializationApi
@Serializable
data class UserData(
    @SerialName("in_wantlist") val inWantlist: Boolean,
    @SerialName("in_collection") val inCollection: Boolean
)

@InternalSerializationApi
@Serializable
data class Community(
    val want: Int,
    val have: Int
)

@InternalSerializationApi
@Serializable
data class FormatDetail(
    val name: String,
    val qty: String,
    val text: String? = null,
    val descriptions: List<String> = emptyList()
)