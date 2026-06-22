package com.cpunks.pizzacatalog.data.remote.api

import com.cpunks.pizzacatalog.data.remote.dto.PizzaListResponse
import retrofit2.http.GET

interface PizzaApi {
    @GET("pizzas")
    suspend fun getPizzas(): PizzaListResponse
}
