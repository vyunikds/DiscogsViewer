package com.example.network.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class ReleaseDetailsDto(
    val id: Int,
    val title: String,
    val artists: List<ArtistDetail> = emptyList(),
    @SerialName("artists_names") val artistsNames: List<String> = emptyList(),
    val country: String = "",
    val genres: List<String> = emptyList(),
    @SerialName("cover_image") val coverImage: String = "",
    val thumb: String = "",
    val images: List<ImageDetail> = emptyList(),
    val community: Community? = null,
)

@InternalSerializationApi
@Serializable
data class ImageDetail(
    val type: String = "",
    val uri: String = "",
    @SerialName("uri150") val uri150: String = "",
    val width: Int = 0,
    val height: Int = 0,
)

@InternalSerializationApi
@Serializable
data class ArtistDetail(
    val name: String = "",
    val joiner: String = "",
    val role: String = "",
    @SerialName("track positions") val trackPositions: String = "",
)
