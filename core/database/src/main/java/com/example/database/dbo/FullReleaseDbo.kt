package com.example.database.dbo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class FullReleaseDbo(
    @Embedded val release: ReleaseDbo,
    @Relation(
        parentColumn = "release_id",
        entityColumn = "genre",
        associateBy = Junction(value = ReleaseGenreDbo::class)
    )
    val genresList: List<GenreDbo>,
    @Relation(
        parentColumn = "release_id",
        entityColumn = "country",
        associateBy = Junction(value = ReleaseCountryDbo::class)
    )
    val countriesList: List<CountryDbo>
)
