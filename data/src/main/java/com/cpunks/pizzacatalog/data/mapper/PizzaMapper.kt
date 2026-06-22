package com.cpunks.pizzacatalog.data.mapper

import com.cpunks.pizzacatalog.core.database.entity.PizzaEntity
import com.cpunks.pizzacatalog.data.remote.dto.PizzaDto
import com.cpunks.pizzacatalog.data.remote.dto.VariantDto
import com.cpunks.pizzacatalog.domain.model.Pizza
import com.cpunks.pizzacatalog.domain.model.PizzaVariant
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()

fun PizzaDto.toDomain(): Pizza = Pizza(
    id          = id,
    name        = name,
    description = description,
    imageUrl    = imageUrl,
    variants    = variants.map { PizzaVariant(it.size, it.price) },
    defaultSize = defaultSize
)

fun PizzaDto.toEntity(): PizzaEntity = PizzaEntity(
    id           = id,
    name         = name,
    description  = description,
    imageUrl     = imageUrl,
    defaultSize  = defaultSize,
    variantsJson = gson.toJson(variants)
)

fun PizzaEntity.toDomain(): Pizza {
    val type     = object : TypeToken<List<VariantDto>>() {}.type
    val variants = gson.fromJson<List<VariantDto>>(variantsJson, type)
    return Pizza(
        id          = id,
        name        = name,
        description = description,
        imageUrl    = imageUrl,
        variants    = variants.map { PizzaVariant(it.size, it.price) },
        defaultSize = defaultSize
    )
}
