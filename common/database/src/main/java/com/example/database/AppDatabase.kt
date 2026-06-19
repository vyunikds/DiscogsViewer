package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.converters.Converters
import com.example.database.dao.TopReleasesDao
import com.example.database.dao.FavoritesDao
import com.example.database.dbo.FavoriteDbo
import com.example.database.dbo.TopReleasesDbo
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@TypeConverters(Converters::class)
@Database(
    entities = [TopReleasesDbo::class, FavoriteDbo::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTopReleasesDao(): TopReleasesDao

    abstract fun getFavoritesDao(): FavoritesDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    ).build()
                }
            }

            return instance!!
        }
    }

}

private const val DATABASE_NAME = "discogs_database"
