package com.example.database.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.database.Table

@Entity(
    tableName = Table.RELEASES_GENRES,
    primaryKeys = ["release_id", "genre"],
    foreignKeys = [
        ForeignKey(
            entity = ReleaseDbo::class,
            parentColumns = ["release_id"],
            childColumns = ["release_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GenreDbo::class,
            parentColumns = ["genre"],
            childColumns = ["genre"]
        )
    ],
    indices = [Index("genre")]
)
data class ReleaseGenreDbo(
    @ColumnInfo(name = "release_id") val releaseId: String,
    @ColumnInfo(name = "genre") val genre: String
)
