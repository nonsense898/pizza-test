package com.cpunks.pizzacatalog.domain.model

data class Pizza(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val variants: List<PizzaVariant>,
    val defaultSize: String
)

data class PizzaVariant(
    val size: String,
    val price: Double
)
