package com.example.database.di

import android.content.Context
import com.example.database.AppDatabase
import com.example.database.dao.FavoritesDao
import com.example.database.dao.TopReleasesDao
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
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideTopReleasesDao(database: AppDatabase): TopReleasesDao = database.getTopReleasesDao()

    @Provides
    fun provideFavoritesDao(database: AppDatabase): FavoritesDao = database.getFavoritesDao()
}