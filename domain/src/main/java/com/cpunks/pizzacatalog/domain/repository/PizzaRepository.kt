package com.cpunks.pizzacatalog.domain.repository

import com.cpunks.pizzacatalog.domain.model.Pizza
import kotlinx.coroutines.flow.Flow

interface PizzaRepository {

    fun getPizzas(): Flow<List<Pizza>>
    suspend fun refreshPizzas()
}
