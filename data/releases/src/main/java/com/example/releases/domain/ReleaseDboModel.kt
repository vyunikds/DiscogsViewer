package com.example.releases.domain

data class ReleaseDboModel(
    val id: String,
    val artistTitle: String,
    val releaseTitle: String,
    val country: String,
    val genres: List<String>,
    val thumb: String,
    val coverImage: String,
    val communityHave: Int = 0,
    val communityWant: Int = 0,
)
