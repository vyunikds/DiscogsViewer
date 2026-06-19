package com.example.releases

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "releases")
@Serializable
data class ReleaseEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val country: String,
    val genre: String,
    val format: String,
    val style: String,
    val label: String,
    val type: String,
    val barcode: String,
    val masterId: Int? = null,
    val masterUrl: String? = null,
    val uri: String,
    val catno: String,
    val thumb: String,
    val coverImage: String,
    val resourceUrl: String,
    val formatQuantity: Int,
    val inCollection: Boolean = false,
    val inWantlist: Boolean = false,
    val communityHave: Int = 0,
    val communityWant: Int = 0
)