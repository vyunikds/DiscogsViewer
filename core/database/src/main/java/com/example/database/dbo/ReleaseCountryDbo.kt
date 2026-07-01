package com.example.database.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.database.Table

@Entity(
    tableName = Table.RELEASES_COUNTRIES,
    primaryKeys = ["release_id", "country"],
    foreignKeys = [
        ForeignKey(
            entity = ReleaseDbo::class,
            parentColumns = ["release_id"],
            childColumns = ["release_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CountryDbo::class,
            parentColumns = ["country"],
            childColumns = ["country"]
        )
    ],
    indices = [Index("country")]
)
data class ReleaseCountryDbo(
    @ColumnInfo(name = "release_id") val releaseId: String,
    @ColumnInfo(name = "country") val country: String
)
