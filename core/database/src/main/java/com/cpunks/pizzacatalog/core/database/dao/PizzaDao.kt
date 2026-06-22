package com.cpunks.pizzacatalog.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cpunks.pizzacatalog.core.database.entity.PizzaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PizzaDao {
    @Query("SELECT * FROM pizzas")
    fun observeAll(): Flow<List<PizzaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(pizzas: List<PizzaEntity>)

    @Query("DELETE FROM pizzas")
    suspend fun deleteAll()
}
