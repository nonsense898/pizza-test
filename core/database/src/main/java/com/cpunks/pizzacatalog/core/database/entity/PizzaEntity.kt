package com.cpunks.pizzacatalog.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pizzas")
data class PizzaEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val defaultSize: String,

    val variantsJson: String
)
