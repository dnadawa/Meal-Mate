package com.w1866973.meal_mate

import androidx.room.TypeConverter

class TypeConverters {
    @TypeConverter
    fun fromIngredientsList(value: ArrayList<Ingredient>): String {
        return value.joinToString(separator = ",") { "${it.name}:${it.measure}" }
    }

    @TypeConverter
    fun toIngredientsList(value: String): ArrayList<Ingredient> {
        val ingredients = ArrayList<Ingredient>()
        value.split(",").forEach {
            val parts = it.split(":")
            ingredients.add(Ingredient(parts[0], parts[1]))
        }
        return ingredients
    }
}
