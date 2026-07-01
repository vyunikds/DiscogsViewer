package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.FavoritesDao
import com.example.database.dao.ReleaseDao
import com.example.database.dao.TopReleaseDao
import com.example.database.dbo.CountryDbo
import com.example.database.dbo.FavoriteDbo
import com.example.database.dbo.GenreDbo
import com.example.database.dbo.ReleaseCountryDbo
import com.example.database.dbo.ReleaseDbo
import com.example.database.dbo.ReleaseGenreDbo
import com.example.database.dbo.TopReleaseDbo

@Database(
    entities = [
        ReleaseDbo::class,
        GenreDbo::class,
        CountryDbo::class,
        ReleaseGenreDbo::class,
        ReleaseCountryDbo::class,
        FavoriteDbo::class,
        TopReleaseDbo::class,
    ],
    version = 5,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getReleaseDao(): ReleaseDao
    abstract fun getFavoritesDao(): FavoritesDao
    abstract fun getTopReleaseDao(): TopReleaseDao

    companion object
}
