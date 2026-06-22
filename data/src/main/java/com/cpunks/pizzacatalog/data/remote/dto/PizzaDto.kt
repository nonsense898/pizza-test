package com.cpunks.pizzacatalog.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PizzaListResponse(@SerializedName("pizzas") val pizzas: List<PizzaDto>)

data class PizzaDto(
    @SerializedName("id")           val id: String,
    @SerializedName("name")         val name: String,
    @SerializedName("description")  val description: String,
    @SerializedName("image_url")    val imageUrl: String,
    @SerializedName("variants")     val variants: List<VariantDto>,
    @SerializedName("default_size") val defaultSize: String
)

data class VariantDto(
    @SerializedName("size")  val size: String,
    @SerializedName("price") val price: Double
)
