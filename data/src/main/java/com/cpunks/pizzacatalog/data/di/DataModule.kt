package com.cpunks.pizzacatalog.data.di

import com.cpunks.pizzacatalog.data.remote.api.PizzaApi
import com.cpunks.pizzacatalog.data.repository.PizzaRepositoryImpl
import com.cpunks.pizzacatalog.domain.repository.PizzaRepository
import com.cpunks.pizzacatalog.domain.usecase.GetPizzasUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton
    abstract fun bindPizzaRepository(impl: PizzaRepositoryImpl): PizzaRepository

    companion object {
        @Provides @Singleton
        fun providePizzaApi(retrofit: Retrofit): PizzaApi =
            retrofit.create(PizzaApi::class.java)

        @Provides @Singleton
        fun provideGetPizzasUseCase(repo: PizzaRepository): GetPizzasUseCase =
            GetPizzasUseCase(repo)
    }
}
