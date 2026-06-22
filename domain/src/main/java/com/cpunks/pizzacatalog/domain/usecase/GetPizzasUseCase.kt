package com.cpunks.pizzacatalog.domain.usecase

import com.cpunks.pizzacatalog.domain.model.Pizza
import com.cpunks.pizzacatalog.domain.repository.PizzaRepository
import kotlinx.coroutines.flow.Flow

class GetPizzasUseCase(private val repository: PizzaRepository) {
    operator fun invoke(): Flow<List<Pizza>> = repository.getPizzas()
}
