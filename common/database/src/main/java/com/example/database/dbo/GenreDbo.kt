package com.example.database.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.Table

@Entity(tableName = Table.GENRES)
data class GenreDbo(
    @PrimaryKey val genre: String
)
