package com.example.discogsviewer.search.domain

data class ReleaseSearch(
    val id: String,
    val artistTitle: String,
    val releaseTitle: String,
    val country: String,
    val genre: List<String>,
    val thumb: String,
)
