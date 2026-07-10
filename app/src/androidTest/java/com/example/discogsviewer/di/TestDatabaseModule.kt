package com.example.discogsviewer.di

import android.content.Context
import androidx.room.Room
import com.example.database.AppDatabase
import com.example.database.dao.FavoritesDao
import com.example.database.dao.ReleaseDao
import com.example.database.dao.TopReleaseDao
import com.example.database.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class],
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
            )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideReleaseDao(database: AppDatabase): ReleaseDao = database.getReleaseDao()

    @Provides
    fun provideFavoritesDao(database: AppDatabase): FavoritesDao = database.getFavoritesDao()

    @Provides
    fun provideTopReleaseDao(database: AppDatabase): TopReleaseDao = database.getTopReleaseDao()
}
