package com.example.database.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.Table

@Entity(tableName = Table.COUNTRIES)
data class CountryDbo(
    @PrimaryKey val country: String
)
