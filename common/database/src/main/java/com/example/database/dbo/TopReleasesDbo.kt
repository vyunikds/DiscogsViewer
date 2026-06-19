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
    @ColumnInfo(name = "cover_image") val coverImage: String,
//    val resourceUrl: String,
//    val formatQuantity: Int,
//    val inCollection: Boolean = false,
//    val inWantlist: Boolean = false,
    val communityHave: Int = 0,
    val communityWant: Int = 0
)