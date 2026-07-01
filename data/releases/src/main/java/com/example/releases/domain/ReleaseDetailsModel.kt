package com.example.releases.domain

import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDetailsModel(
    val id: String,
    val releaseTitle: String,
    val artistTitle: String,
    val country: String,
    val genres: List<String>,
    val coverImage: String,
    val want: Int,
    val have: Int,
)
