package com.example.search

data class ReleaseSearchResult(
    val id: String,
    val artistTitle: String,
    val releaseTitle: String,
    val country: String,
    val genre: List<String>,
    val thumb: String,
    val hasNextPage: Boolean,
)
