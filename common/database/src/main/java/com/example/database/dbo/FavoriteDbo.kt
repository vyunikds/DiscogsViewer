package com.example.database.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.Table

@Entity(tableName = Table.FAVORITES)
data class FavoriteDbo(
    @PrimaryKey
    val releaseId: String,
    @ColumnInfo(name = "added_at") val addedAt: Long
)
