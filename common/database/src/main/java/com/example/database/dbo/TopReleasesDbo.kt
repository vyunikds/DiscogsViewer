package com.example.database.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.Table
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Entity(tableName = Table.TOP_RELEASES)
@Serializable
data class TopReleasesDbo(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "artist_title") val artistTitle: String,
    @ColumnInfo(name = "release_title") val releaseTitle: String,
    val country: String,
    val genres: List<String>,
    val thumb: String,
    @ColumnInfo(name = "cover_image") val coverImage: String,
    val communityHave: Int = 0,
    val communityWant: Int = 0
)