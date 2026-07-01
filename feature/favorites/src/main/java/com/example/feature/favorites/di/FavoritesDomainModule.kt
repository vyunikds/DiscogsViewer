package com.example.feature.favorites.di

import com.example.feature.favorites.domain.LoadFavoritesPageUseCase
import com.example.feature.favorites.domain.LoadFavoritesPageUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesDomainModule {
    @Binds
    abstract fun bindLoadFavoritesPageUseCase(impl: LoadFavoritesPageUseCaseImpl): LoadFavoritesPageUseCase
}
