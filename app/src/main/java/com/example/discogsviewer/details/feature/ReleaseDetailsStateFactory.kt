package com.example.discogsviewer.details.feature

import com.example.discogsviewer.details.domain.ReleaseDetails
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleaseDetailsStateFactory @Inject constructor() {
    fun create(releaseDetails: ReleaseDetails): ReleaseDetailsState {
        return ReleaseDetailsState(
            id = releaseDetails.id,
            releaseTitle = releaseDetails.releaseTitle,
            artistTitle = releaseDetails.artistTitle,
            want = releaseDetails.want,
            have = releaseDetails.have,
            country = releaseDetails.country,
            genres = releaseDetails.genres,
            coverImage = releaseDetails.coverImage,
            isFavorite = releaseDetails.isFavorite,
        )
    }
}
