package com.w1866973.meal_mate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Meal(
    @PrimaryKey() val id: String,
    val meal: String,
    val drinkAlternate: String?,
    val category: String,
    val area: String,
    val instructions: String,
    val mealThumb: String,
    val tags: String?,
    val youtube: String,
    val ingredients: ArrayList<Ingredient>,
    val source: String?,
    val imageSource: String?,
    val creativeCommonsConfirmed: String?,
    val dateModified: String?,
)
