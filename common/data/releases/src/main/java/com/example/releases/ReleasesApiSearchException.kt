package com.example.releases

class ReleasesApiSearchException(cause: Throwable? = null) :
    Exception("Failed to search releases from API", cause)