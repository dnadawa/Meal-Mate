package com.w1866973.meal_mate

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(vararg meal: Meal)

    @Query("SELECT * FROM Meal WHERE LOWER(meal) LIKE LOWER('%' || :searchString || '%') OR LOWER(ingredients) LIKE LOWER('%' || :searchString || '%')")
    fun searchMeals(searchString: String): List<Meal>
}