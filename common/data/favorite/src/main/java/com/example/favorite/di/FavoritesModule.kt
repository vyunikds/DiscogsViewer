package com.example.favorite.di

import com.example.favorite.FavoritesRepository
import com.example.favorite.FavoritesRepositoryImpl
import com.example.favorite.ToggleFavoriteUseCase
import com.example.favorite.ToggleFavoriteUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesModule {
    @Binds
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    abstract fun bindToggleFavoriteUseCase(impl: ToggleFavoriteUseCaseImpl): ToggleFavoriteUseCase
}