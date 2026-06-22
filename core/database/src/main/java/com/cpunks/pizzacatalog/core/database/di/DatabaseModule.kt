package com.cpunks.pizzacatalog.core.database.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cpunks.pizzacatalog.core.database.dao.PizzaDao
import com.cpunks.pizzacatalog.core.database.entity.PizzaEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [PizzaEntity::class], version = 1, exportSchema = false)
abstract class PizzaDatabase : RoomDatabase() {
    abstract fun pizzaDao(): PizzaDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): PizzaDatabase =
        Room.databaseBuilder(ctx, PizzaDatabase::class.java, "pizza_db").build()

    @Provides @Singleton
    fun providePizzaDao(db: PizzaDatabase): PizzaDao = db.pizzaDao()
}
