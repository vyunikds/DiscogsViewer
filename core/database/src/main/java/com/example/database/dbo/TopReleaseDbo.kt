package com.example.database.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.Table

@Entity(tableName = Table.TOP_RELEASES)
data class TopReleaseDbo(
    @PrimaryKey @ColumnInfo(name = "releaseId") val releaseId: String
)
