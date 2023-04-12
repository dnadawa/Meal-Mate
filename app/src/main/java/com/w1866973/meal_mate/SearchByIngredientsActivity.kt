package com.w1866973.meal_mate

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SearchByIngredientsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_ingredients)
    }


    fun onRetrieveMealsClicked(view: View) {
        val txtSearch = findViewById<EditText>(R.id.txtSearch)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val cardsListView = findViewById<LinearLayout>(R.id.cardsList)
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
            cardsListView.removeAllViews()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val fetchedData: JSONObject =
                        sendHttpGetRequest("https://www.themealdb.com/api/json/v1/1/filter.php?i=$searchText")

                    if (!fetchedData.isNull("meals")) {
                        val mealsList: JSONArray = fetchedData.getJSONArray("meals")

                        val idList: ArrayList<String> = arrayListOf()
                        for (i in 0 until mealsList.length()) {
                            val meal: JSONObject = mealsList[i] as JSONObject
                            idList.add(meal.getString("idMeal"))
                        }

                        val fullMealsList: ArrayList<JSONObject> = arrayListOf()
                        for (id in idList) {
                            val mealData: JSONObject = sendHttpGetRequest("https://www.themealdb.com/api/json/v1/1/lookup.php?i=$id")
                            if (!mealData.isNull("meals")) {
                                val mealsList: JSONArray = mealData.getJSONArray("meals")
                                val meal: JSONObject = mealsList[0] as JSONObject
                                fullMealsList.add(meal)
                                val mealObj = Meal.fromJson(meal)
                                withContext(Dispatchers.Main){
                                    val textView = LayoutInflater.from(applicationContext).inflate(R.layout.card, cardsListView, false) as TextView
                                    textView.text = mealObj.toString()
                                    cardsListView.addView(textView)
                                }
                            }
                        }


                        withContext(Dispatchers.Main){
                            progressBar.visibility = View.GONE
                        }

                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "No data found!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
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
}