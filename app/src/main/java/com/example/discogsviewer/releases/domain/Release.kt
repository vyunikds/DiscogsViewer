package com.example.discogsviewer.releases.domain

data class Release (
    val id: Int,
    val artistTitle: String,
    val releaseTitle: String,
    val country: String,
    val genre: List<String>,
    val thumb: String,
    val coverImage: String = "",
)
