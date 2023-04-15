package com.w1866973.meal_mate

import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SearchForMealsActivity : AppCompatActivity() {
    var fromWeb: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_for_meals)

        fromWeb = intent.getBooleanExtra("fromWeb", false)

        //make status bar hide in landscape mode
        //https://stackoverflow.com/questions/11856886/hiding-title-bar-notification-bar-when-device-is-oriented-to-landscape
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun onSearchButtonPressed(view: View) {
        val txtSearch = findViewById<EditText>(R.id.txtSearchForMeal)
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
            val linearLayout = findViewById<LinearLayout>(R.id.searchForMealsCardsList)
            val progressBar: ProgressBar = findViewById(R.id.searchForMealsProgress)


            linearLayout.removeAllViews()
            progressBar.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var meals: List<Meal> = mutableListOf()
                    if (!fromWeb) {
                        meals = searchFromDB(searchText.toString())
                    } else {
                        val apiService = APIService()
                        val jsonArray: JSONArray =
                            apiService.getMealsList("https://www.themealdb.com/api/json/v1/1/search.php?s=$searchText")
                        val mutableList = meals.toMutableList()
                        for (i in 0 until jsonArray.length()) {
                            val jsonMeal: JSONObject = jsonArray[i] as JSONObject
                            val meal: Meal = Meal.fromJson(jsonMeal)
                            mutableList.add(meal)
                        }
                        meals = mutableList.toList()
                    }

                    withContext(Dispatchers.Main) {
                        if (meals.isEmpty()) {
                            throw Exception("No meals found!")
                        } else {
                            val cards: ArrayList<LinearLayout> = arrayListOf()

                            for (meal in meals) {
                                val cardLinearLayout: LinearLayout =
                                    LayoutInflater.from(applicationContext)
                                        .inflate(R.layout.card, linearLayout, false) as LinearLayout
                                val textView: TextView =
                                    cardLinearLayout.findViewById(R.id.cardTextView)
                                val imageView: ImageView =
                                    cardLinearLayout.findViewById(R.id.cardImageView)
                                textView.text = meal.toString()

                                try {
                                    val bitmap = withContext(Dispatchers.IO) {
                                        val url = URL(meal.mealThumb)
                                        val connection = url.openConnection() as HttpURLConnection
                                        connection.doInput = true
                                        connection.connect()
                                        val inputStream = connection.inputStream
                                        val bitmap = BitmapFactory.decodeStream(inputStream)
                                        inputStream.close()
                                        connection.disconnect()
                                        bitmap
                                    }
                                    imageView.setImageBitmap(bitmap)
                                } catch (e: Exception) {
                                    imageView.setImageResource(R.drawable.logo2)
                                }
                                cards.add(cardLinearLayout)
                            }

                            for (card in cards) {
                                linearLayout.addView(card)
                            }
                            progressBar.visibility = View.GONE
                        }
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
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

    private fun searchFromDB(searchText: String): List<Meal> {
        val appDatabase: AppDatabase = AppDatabase.getDatabase(this)
        val mealDao: MealDao = appDatabase.mealDao()
        return mealDao.searchMeals(searchText)
    }
}