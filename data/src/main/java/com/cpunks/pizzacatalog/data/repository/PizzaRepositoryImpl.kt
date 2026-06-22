package com.cpunks.pizzacatalog.data.repository

import com.cpunks.pizzacatalog.core.database.dao.PizzaDao
import com.cpunks.pizzacatalog.data.mapper.toDomain
import com.cpunks.pizzacatalog.data.mapper.toEntity
import com.cpunks.pizzacatalog.data.remote.api.PizzaApi
import com.cpunks.pizzacatalog.domain.model.Pizza
import com.cpunks.pizzacatalog.domain.repository.PizzaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PizzaRepositoryImpl @Inject constructor(
    private val api: PizzaApi,
    private val dao: PizzaDao
) : PizzaRepository {

    override fun getPizzas(): Flow<List<Pizza>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshPizzas() {
        val remote = api.getPizzas().pizzas
        dao.upsertAll(remote.map { it.toEntity() })
    }
}
