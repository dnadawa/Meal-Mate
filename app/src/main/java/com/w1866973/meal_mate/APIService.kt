package com.w1866973.meal_mate

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class APIService {
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

    fun getMealsList(url: String): JSONArray {
        val fetchedData: JSONObject = sendHttpGetRequest(url)
        if (fetchedData.isNull("meals")) {
            throw Exception("No meals found!");
        }

        return fetchedData.getJSONArray("meals")
    }

    fun changeLoadingState(vararg buttons: Button, progressBar: ProgressBar, isLoading: Boolean){
        for(btn in buttons){
            btn.isEnabled = !isLoading
        }
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}