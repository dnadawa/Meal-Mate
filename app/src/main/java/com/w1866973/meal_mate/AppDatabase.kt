package com.w1866973.meal_mate

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Meal::class], version = 1)
@TypeConverters(com.w1866973.meal_mate.TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
}