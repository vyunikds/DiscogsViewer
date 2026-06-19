package com.example.favorite

import com.example.database.dbo.FavoriteDbo
import javax.inject.Inject

class FavoriteDboMapper @Inject constructor() {
    fun toDomain(dbo: FavoriteDbo): FavoriteItem = FavoriteItem(
        releaseId = dbo.releaseId,
        artistTitle = dbo.artistTitle,
        releaseTitle = dbo.releaseTitle,
        country = dbo.country,
        genres = dbo.genres,
        thumb = dbo.thumb,
        coverImage = dbo.coverImage,
        communityHave = dbo.communityHave,
        communityWant = dbo.communityWant,
        addedAt = dbo.addedAt,
    )
}