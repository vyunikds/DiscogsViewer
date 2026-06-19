package com.example.network.api

class ReleasesApiFetchException(cause: Throwable? = null) :
    Exception("Failed to fetch releases from API", cause)