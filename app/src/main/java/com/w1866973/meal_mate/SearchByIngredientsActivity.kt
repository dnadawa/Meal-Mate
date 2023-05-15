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

    private lateinit var fetchedMealsList: ArrayList<Meal>
    private lateinit var retrieveButton: Button
    private lateinit var saveToDatabaseButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var txtSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_ingredients)

        fetchedMealsList = arrayListOf()
        retrieveButton = findViewById(R.id.btnRetrieveMeals)
        saveToDatabaseButton = findViewById(R.id.btnSaveToDB)
        progressBar = findViewById(R.id.progressBar)
        txtSearch = findViewById(R.id.txtSearch)

        if(savedInstanceState != null){
            val isLoading: Boolean = savedInstanceState.getBoolean("isLoading")
            val searchText: String = savedInstanceState.getString("searchText", "")

            if(isLoading){
                fetchDataFromAPI(searchText)
            } else{
                fetchedMealsList = savedInstanceState.getSerializable("mealsList")!! as ArrayList<Meal>
                if(fetchedMealsList.isNotEmpty()){
                    addCards(fetchedMealsList, findViewById(R.id.cardsList))
                }
            }
        }

        //make status bar hide in landscape mode
        //https://stackoverflow.com/questions/11856886/hiding-title-bar-notification-bar-when-device-is-oriented-to-landscape
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isLoading", !retrieveButton.isEnabled)
        outState.putString("searchText", txtSearch.text.trim().toString())
        outState.putSerializable("mealsList", fetchedMealsList)
    }


    fun onRetrieveMealsClicked(view: View) {
        val searchText = txtSearch.text.trim()

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
            fetchDataFromAPI(searchText.toString())
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

    private fun fetchDataFromAPI(searchText: String) {
        val apiService = APIService()
        val linearLayout = findViewById<LinearLayout>(R.id.cardsList)

        apiService.changeLoadingState(
            retrieveButton,
            saveToDatabaseButton,
            progressBar = progressBar,
            editText = txtSearch,
            isLoading = true
        )
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
                    apiService.changeLoadingState(
                        retrieveButton,
                        saveToDatabaseButton,
                        progressBar = progressBar,
                        editText = txtSearch,
                        isLoading = false
                    )

                    addCards(fetchedMealsList, linearLayout)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    apiService.changeLoadingState(
                        retrieveButton,
                        saveToDatabaseButton,
                        progressBar = progressBar,
                        editText = txtSearch,
                        isLoading = false
                    )

                    Toast.makeText(
                        applicationContext,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addCards(fetchedMealsList: ArrayList<Meal>, linearLayout: LinearLayout){
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
}