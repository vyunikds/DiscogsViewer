package com.example.database.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.Table
import kotlinx.serialization.Serializable

@Entity(tableName = Table.FAVORITES)
@Serializable
data class FavoriteDbo(
    @PrimaryKey
    val releaseId: String,
    @ColumnInfo(name = "artist_title") val artistTitle: String = "",
    @ColumnInfo(name = "release_title") val releaseTitle: String = "",
    val country: String = "",
    val genres: List<String> = emptyList(),
    val thumb: String = "",
    @ColumnInfo(name = "cover_image") val coverImage: String? = null,
    val communityHave: Int = 0,
    val communityWant: Int = 0,
    @ColumnInfo(name = "added_at") val addedAt: Long
)
