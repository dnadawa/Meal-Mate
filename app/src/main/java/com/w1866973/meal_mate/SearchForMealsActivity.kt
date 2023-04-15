package com.w1866973.meal_mate

import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var fromWeb: Boolean = false
    private lateinit var searchButton: Button
    private lateinit var txtSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var linearLayout: LinearLayout
    private lateinit var cards: ArrayList<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_for_meals)

        searchButton = findViewById(R.id.btnSearch)
        txtSearch = findViewById(R.id.txtSearchForMeal)
        linearLayout = findViewById(R.id.searchForMealsCardsList)
        progressBar = findViewById(R.id.searchForMealsProgress)
        cards = arrayListOf()

        if (savedInstanceState != null) {
            val isLoading: Boolean = savedInstanceState.getBoolean("isLoading")
            val searchText: String = savedInstanceState.getString("searchText", "")
            fromWeb = savedInstanceState.getBoolean("fromWeb")

            if (isLoading) {
                search(searchText, fromWeb)
            } else {
                cards = savedInstanceState.getSerializable("cards")!! as ArrayList<LinearLayout>
                linearLayout.removeAllViews()
                for (card in cards) {
                    (card.parent as? ViewGroup)?.removeView(card)
                    linearLayout.addView(card)
                }
            }

        } else {
            fromWeb = intent.getBooleanExtra("fromWeb", false)
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
        outState.putBoolean("isLoading", !searchButton.isEnabled)
        outState.putBoolean("fromWeb", fromWeb)
        outState.putString("searchText", txtSearch.text.trim().toString())
        outState.putSerializable("cards", cards)
    }

    fun onSearchButtonPressed(view: View) {
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
            search(searchText.toString(), fromWeb)
        }
    }

    private fun search(searchText: String, fromWeb: Boolean) {
        val apiService = APIService()

        linearLayout.removeAllViews()
        apiService.changeLoadingState(
            searchButton,
            progressBar = progressBar,
            editText = txtSearch,
            isLoading = true
        )

        CoroutineScope(Dispatchers.IO).launch {
            val fetchedMeals: List<Meal>
            try {
                if (!fromWeb) {
                    fetchedMeals = searchFromDB(searchText)
                } else {
                    fetchedMeals = searchFromWeb(searchText, apiService)
                }

                withContext(Dispatchers.Main) {
                    if (fetchedMeals.isEmpty()) {
                        throw Exception("No meals found!")
                    } else {
                        addCards(fetchedMeals)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    apiService.changeLoadingState(
                        searchButton,
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

    private fun searchFromDB(searchText: String): List<Meal> {
        val appDatabase: AppDatabase = AppDatabase.getDatabase(this)
        val mealDao: MealDao = appDatabase.mealDao()
        return mealDao.searchMeals(searchText)
    }

    private fun searchFromWeb(searchText: String, apiService: APIService): List<Meal> {
        val jsonArray: JSONArray =
            apiService.getMealsList("https://www.themealdb.com/api/json/v1/1/search.php?s=$searchText")
        val mutableList: MutableList<Meal> = mutableListOf()
        for (i in 0 until jsonArray.length()) {
            val jsonMeal: JSONObject = jsonArray[i] as JSONObject
            val meal: Meal = Meal.fromJson(jsonMeal)
            mutableList.add(meal)
        }
        return mutableList.toList()
    }

    private suspend fun addCards(fetchedMeals: List<Meal>) {
        val apiService = APIService()

        for (meal in fetchedMeals) {
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

        apiService.changeLoadingState(
            searchButton,
            progressBar = progressBar,
            editText = txtSearch,
            isLoading = false
        )
    }


}