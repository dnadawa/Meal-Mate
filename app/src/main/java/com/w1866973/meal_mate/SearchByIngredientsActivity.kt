package com.w1866973.meal_mate

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class SearchByIngredientsActivity : AppCompatActivity() {

    private val fetchedMealsList: ArrayList<Meal> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_ingredients)

        //make status bar hide in landscape mode
        //https://stackoverflow.com/questions/11856886/hiding-title-bar-notification-bar-when-device-is-oriented-to-landscape
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }


    fun onRetrieveMealsClicked(view: View) {
        val txtSearch = findViewById<EditText>(R.id.txtSearch)
        val linearLayout = findViewById<LinearLayout>(R.id.cardsList)
        val retrieveButton = findViewById<Button>(R.id.btnRetrieveMeals)
        val saveToDB = findViewById<Button>(R.id.btnSaveToDB)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val searchText = txtSearch.text.trim()
        val apiService = APIService()

        txtSearch.clearFocus()
        if (searchText.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please enter a search text!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (!searchText.matches("^[a-zA-Z]*$".toRegex())) {
            Toast.makeText(
                applicationContext,
                "Please enter only letters!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            apiService.changeLoadingState(retrieveButton, saveToDB, progressBar = progressBar, isLoading = true)
            linearLayout.removeAllViews()
            fetchedMealsList.clear()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val fetchedMeals: JSONArray =
                        apiService.getMealsList("https://www.themealdb.com/api/json/v1/1/filter.php?i=$searchText")

                    for (i in 0 until fetchedMeals.length()) {
                        val meal: JSONObject = fetchedMeals[i] as JSONObject
                        val id = meal.getString("idMeal")

                        val detailedMeals: JSONArray =
                            apiService.getMealsList("https://www.themealdb.com/api/json/v1/1/lookup.php?i=$id")

                        val fetchedDetailedMeal: JSONObject = detailedMeals[0] as JSONObject
                        val mealObj: Meal = Meal.fromJson(fetchedDetailedMeal)
                        fetchedMealsList.add(mealObj)
                    }

                    withContext(Dispatchers.Main) {
                        apiService.changeLoadingState(retrieveButton, saveToDB, progressBar = progressBar, isLoading = false)

                        for (meal in fetchedMealsList) {
                            val cardLinearLayout: LinearLayout =
                                LayoutInflater.from(applicationContext)
                                    .inflate(R.layout.card, linearLayout, false) as LinearLayout
                            cardLinearLayout.findViewById<TextView>(R.id.cardTextView).text =
                                meal.toString()
                            cardLinearLayout.findViewById<ImageView>(R.id.cardImageView).visibility =
                                View.GONE
                            linearLayout.addView(cardLinearLayout)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        apiService.changeLoadingState(retrieveButton, saveToDB, progressBar = progressBar, isLoading = true)

                        Toast.makeText(
                            applicationContext,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun onSaveToDBButtonClicked(view: View) {
        val appDatabase: AppDatabase = AppDatabase.getDatabase(this)
        val mealDao: MealDao = appDatabase.mealDao()

        if (fetchedMealsList.isEmpty()) {
            Toast.makeText(applicationContext, "No meals fetched!", Toast.LENGTH_SHORT)
                .show()
        } else {

            CoroutineScope(Dispatchers.IO).launch {
                for (meal in fetchedMealsList) {
                    mealDao.insertMeals(meal)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Successfully added to the database!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }
}