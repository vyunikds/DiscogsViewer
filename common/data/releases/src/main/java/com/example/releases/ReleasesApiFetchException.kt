package com.example.releases

class ReleasesApiFetchException(cause: Throwable? = null) :
    Exception("Failed to fetch releases from API", cause)