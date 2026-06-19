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
    @ColumnInfo(name = "artist_title") val artistTitle: String,
    @ColumnInfo(name = "release_title") val releaseTitle: String,
    val country: String,
    val genres: List<String>,
//    val format: String,
//    val style: String,
//    val label: String,
//    val type: String,
//    val barcode: String,
//    val masterId: Int? = null,
//    val masterUrl: String? = null,
//    val uri: String,
//    val catno: String,
    val thumb: String,
//    val isFavorite: Boolean,
    @ColumnInfo(name = "cover_image") val coverImage: String?,
//    val resourceUrl: String,
//    val formatQuantity: Int,
//    val inCollection: Boolean = false,
//    val inWantlist: Boolean = false,
    val communityHave: Int = 0,
    val communityWant: Int = 0,
    @ColumnInfo(name = "added_at") val addedAt: Long
)
