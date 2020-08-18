package com.example.notfallapp.server

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.notfallapp.BroadcastReciever.ActionsBracelet
import com.example.notfallapp.alarm.AlarmFailedActivity
import com.example.notfallapp.alarm.AlarmSuccessfulActivity
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.login.LoginActivity
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Timestamp
import java.util.*

/**
 * class that has the function to send a alarm and the position to the server
 */
class ServerCallAlarm {
    companion object {
        private lateinit var sharedPreferences: SharedPreferences
        var serverAPIURL = "https://jamesdev.ilogs.com/API/v1"

        //var serverAPIURL = "https://safemotiondev.ilogs.com/API/v1"
        const val TAG = "ServerApi"
        private var volleyRequestQueue: RequestQueue? = null
        var userId: String? = null

        /**
         * function send a alarm to the server and handle the response
         */
        fun sendAlarm(context: Context) {
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            reqBody.put("Type", 0)
            if (ActionsBracelet.batteryState == 0) {
                reqBody.put("Battery", null)
            } else {
                reqBody.put("Battery", ActionsBracelet.batteryState)
            }
            sharedPreferences = LoginActivity.sharedPreferences!!
            userId = sharedPreferences.getString("UserId", "")
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.POST, "${ServerApi.serverAPIURL}/users/${userId}/alert", reqBody,
                Response.Listener { response ->
                    Log.e(ServerApi.TAG, "response: $response")
                    val intent = Intent(context, AlarmSuccessfulActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                },
                Response.ErrorListener { error ->
                    if (error.networkResponse != null) {
                        val resErrorBody = JSONObject(String(error.networkResponse.data))
                        Log.e(
                            ServerApi.TAG,
                            "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get(
                                "Error"
                            )
                        )
                        val intent = Intent(context, AlarmFailedActivity::class.java)
                        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    } else {
                        Log.e(ServerApi.TAG, "problem occurred, volley error: " + error.message)
                        val intent = Intent(context, AlarmFailedActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>? {
                    val params = HashMap<String, String>()
                    val token = sharedPreferences.getString("AccessToken", "")
                    params["Authorization"] = "Bearer $token"
                    //..add other headers
                    return params
                }
            }
            volleyRequestQueue?.add(jsonObjectRequest)
        }

        /**
         * function send the position of the user to the server and handle the response
         */
        fun sendPosition(context: Context){
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            val body = JSONObject()
            val arrayBody = JSONArray()
            val time = Timestamp(System.currentTimeMillis()).toString()
            val times = time.split(" ")
            val currentTime = times[0]+"T"+times[1]+"+00:00"
            val location = CurrentLocation.currentLocation

            body.put("Timestamp", currentTime)
            if(location?.longitude != null){
                body.put("Longitude", location.longitude)
            }else{
                body.put("Longitude", 0)
            }
            if(location?.latitude != null){
                body.put("Latitude", location.latitude)
            }else{
                body.put("Latitude", 0)
            }
            if(location?.accuracy != null){
                body.put("Accuracy", location.accuracy)
            }else{
                body.put("Accuracy", 0)
            }
            body.put("Source", "gps")
            arrayBody.put(body)
            reqBody.put("Positions", arrayBody)
            sharedPreferences = LoginActivity.sharedPreferences!!
            userId = sharedPreferences.getString("UserId", "")
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.POST, "${ServerApi.serverAPIURL}/users/${userId}/positions", reqBody,
                Response.Listener { response ->
                    Log.e(ServerApi.TAG, "response: $response")
                },
                Response.ErrorListener { error ->
                    if (error.networkResponse != null) {
                        val resErrorBody = JSONObject(String(error.networkResponse.data))
                        Log.e(
                            ServerApi.TAG,
                            "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get(
                                "Error"
                            )
                        )
                    } else {
                        if(!error.message.equals("org.json.JSONException: End of input at character 0 of ")) {
                            Log.e(ServerApi.TAG, "problem occurred, volley error: " + error.message)
                        }
                    }
                }) {
                override fun getHeaders(): Map<String, String>? {
                    val params = HashMap<String, String>()
                    val token = sharedPreferences.getString("AccessToken", "")
                    params["Authorization"] = "Bearer $token"
                    //..add other headers
                    return params
                }
            }
            volleyRequestQueue?.add(jsonObjectRequest)
        }
    }
}