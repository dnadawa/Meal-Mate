package com.w1866973.meal_mate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SearchByIngredientsActivity : AppCompatActivity() {

    private val fetchedMealsList: ArrayList<Meal> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_ingredients)
    }


    fun onRetrieveMealsClicked(view: View) {
        val txtSearch = findViewById<EditText>(R.id.txtSearch)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val linearLayout = findViewById<LinearLayout>(R.id.cardsList)
        val searchText = txtSearch.text.trim()

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
            progressBar.visibility = View.VISIBLE
            linearLayout.removeAllViews()
            fetchedMealsList.clear()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val fetchedMeals: JSONArray =
                        getMealsList("https://www.themealdb.com/api/json/v1/1/filter.php?i=$searchText")

                    for (i in 0 until fetchedMeals.length()) {
                        val meal: JSONObject = fetchedMeals[i] as JSONObject
                        val id = meal.getString("idMeal")

                        val detailedMeals: JSONArray =
                            getMealsList("https://www.themealdb.com/api/json/v1/1/lookup.php?i=$id")

                        val fetchedDetailedMeal: JSONObject = detailedMeals[0] as JSONObject
                        val mealObj: Meal = Meal.fromJson(fetchedDetailedMeal)
                        fetchedMealsList.add(mealObj)
                    }

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE

                        for (meal in fetchedMealsList) {
                            val cardLinearLayout: LinearLayout = LayoutInflater.from(applicationContext)
                                .inflate(R.layout.card, linearLayout, false) as LinearLayout
                            cardLinearLayout.findViewById<TextView>(R.id.cardTextView).text = meal.toString()
                            cardLinearLayout.findViewById<ImageView>(R.id.cardImageView).visibility = View.GONE
                            linearLayout.addView(cardLinearLayout)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE

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

    private fun sendHttpGetRequest(url: String): JSONObject {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Failed to connect. Error code: ${connection.responseCode}")
        }

        val response = StringBuilder()
        connection.inputStream.bufferedReader().useLines { lines ->
            lines.forEach {
                response.append(it)
            }
        }

        return JSONObject(response.toString())
    }

    private fun getMealsList(url: String): JSONArray {
        val fetchedData: JSONObject = sendHttpGetRequest(url)
        if (fetchedData.isNull("meals")) {
            throw Exception("No meals found!");
        }

        return fetchedData.getJSONArray("meals")
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