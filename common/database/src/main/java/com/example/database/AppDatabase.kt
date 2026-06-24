package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.converters.Converters
import com.example.database.dao.FavoritesDao
import com.example.database.dao.ReleaseDao
import com.example.database.dbo.CountryDbo
import com.example.database.dbo.FavoriteDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseDbo
import com.example.database.dbo.ReleaseGenreDbo

@Database(
    entities = [
        ReleaseDbo::class,
        GenreDbo::class,
        CountryDbo::class,
        ReleaseGenreDbo::class,
        ReleaseCountryDbo::class,
        FavoriteDbo::class,
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getReleaseDao(): ReleaseDao
    abstract fun getFavoritesDao(): FavoritesDao

    companion object
}
