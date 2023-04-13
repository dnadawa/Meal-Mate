package com.w1866973.meal_mate

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class SearchForMealsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_for_meals)
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
            val appDatabase: AppDatabase = AppDatabase.getDatabase(this)
            val mealDao: MealDao = appDatabase.mealDao()

            linearLayout.removeAllViews()
            CoroutineScope(Dispatchers.IO).launch {
                val meals: List<Meal> = mealDao.searchMeals(searchText.toString())

                withContext(Dispatchers.Main) {
                    if (meals.isEmpty()) {
                        Toast.makeText(
                            applicationContext,
                            "No meals found!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
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
                            linearLayout.addView(cardLinearLayout)
                        }
                    }
                }
            }
        }
    }
}