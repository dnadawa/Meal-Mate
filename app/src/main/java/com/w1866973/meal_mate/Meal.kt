package com.w1866973.meal_mate

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity
data class Meal(
    @PrimaryKey val id: String,
    val meal: String,
    val drinkAlternate: String?,
    val category: String,
    val area: String,
    val instructions: String,
    val mealThumb: String,
    val tags: String?,
    val youtube: String,
    val ingredients: String,
    val measures: String,
    val source: String?,
    val imageSource: String?,
    val creativeCommonsConfirmed: String?,
    val dateModified: String?,
) {
    companion object {
        fun fromJson(json: JSONObject): Meal {
            val id = json.getString("idMeal")
            val meal = json.getString("strMeal")
            val drinkAlternate =
                if (!json.isNull("strDrinkAlternate")) json.getString("strDrinkAlternate") else null
            val category = json.getString("strCategory")
            val area = json.getString("strArea")
            val instructions = json.getString("strInstructions")
            val mealThumb = json.getString("strMealThumb")
            val tags = if (!json.isNull("strTags")) json.getString("strTags") else null
            val youtube = json.getString("strYoutube")
            val ingredientsArrayList = ArrayList<String>()
            val measuresArrayList = ArrayList<String>()
            var keyIndex = 1
            for (key in json.keys()) {
                if (key.startsWith("strIngredient")) {
                    if (!json.isNull(key) && json.getString(key).isNotEmpty()) {
                        val ingredientName = json.getString(key)
                        val ingredientMeasure = json.getString("strMeasure$keyIndex")
                        ingredientsArrayList.add(ingredientName)
                        measuresArrayList.add(ingredientMeasure)
                        keyIndex++
                    }
                }
            }
            val source = if (!json.isNull("strSource")) json.getString("strSource") else null
            val imageSource =
                if (!json.isNull("strImageSource")) json.getString("strImageSource") else null
            val creativeCommonsConfirmed =
                if (!json.isNull("strCreativeCommonsConfirmed")) json.getString("strCreativeCommonsConfirmed") else null
            val dateModified =
                if (!json.isNull("dateModified")) json.getString("dateModified") else null
            return Meal(
                id,
                meal,
                drinkAlternate,
                category,
                area,
                instructions,
                mealThumb,
                tags,
                youtube,
                ingredientsArrayList.toString(),
                measuresArrayList.toString(),
                source,
                imageSource,
                creativeCommonsConfirmed,
                dateModified
            )
        }
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\"Meal\":\"").append(meal).append("\",\n")
        stringBuilder.append("\"DrinkAlternate\":").append(drinkAlternate?.let { "\"$it\"" } ?: "null").append(",\n")
        stringBuilder.append("\"Category\":\"").append(category).append("\",\n")
        stringBuilder.append("\"Area\":\"").append(area).append("\",\n")
        stringBuilder.append("\"Instructions\":\"").append(instructions).append("\",\n")
        stringBuilder.append("\"Tags\":").append(tags?.let { "\"$it\"" } ?: "null").append(",\n")
        stringBuilder.append("\"Youtube\":\"").append(youtube).append("\",\n")
        val ingredientsList = ingredients.substring(1).split(", ").dropLast(1)
        val measuresList = measures.substring(1).split(", ").dropLast(1)
        for (i in ingredientsList.indices) {
            stringBuilder.append("\"Ingredient").append(i+1).append("\":\"").append(ingredientsList[i]).append("\",\n")
        }
        for (i in measuresList.indices) {
            stringBuilder.append("\"Measure").append(i+1).append("\":\"").append(measuresList[i]).append("\",\n")
        }
        return stringBuilder.toString()
    }
}
