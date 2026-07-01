package com.example.database.di

import android.content.Context
import androidx.room.Room
import com.example.database.AppDatabase
import com.example.database.dao.FavoritesDao
import com.example.database.dao.ReleaseDao
import com.example.database.dao.TopReleaseDao
import com.example.database.migrations.MIGRATION_3_4
import com.example.database.migrations.MIGRATION_4_5
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).addMigrations(MIGRATION_3_4, MIGRATION_4_5)
         .fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun provideReleaseDao(database: AppDatabase): ReleaseDao = database.getReleaseDao()

    @Provides
    fun provideFavoritesDao(database: AppDatabase): FavoritesDao = database.getFavoritesDao()

    @Provides
    fun provideTopReleaseDao(database: AppDatabase): TopReleaseDao = database.getTopReleaseDao()
}

private const val DATABASE_NAME = "discogs_database"
