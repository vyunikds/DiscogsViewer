package com.example.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {

    @Provides
    @Named("ioDispatcher")
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}
