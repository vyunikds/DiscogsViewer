package com.example.database.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.Table

@Entity(tableName = Table.RELEASES)
data class ReleaseDbo(
    @PrimaryKey @ColumnInfo(name = "release_id") val id: String,
    @ColumnInfo(name = "artist_title") val artistTitle: String,
    @ColumnInfo(name = "release_title") val releaseTitle: String,
    val thumb: String,
    @ColumnInfo(name = "cover_image") val coverImage: String,
    @ColumnInfo(name = "community_have") val communityHave: Int = 0,
    @ColumnInfo(name = "community_want") val communityWant: Int = 0
)
