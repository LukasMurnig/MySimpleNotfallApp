package com.example.notfallapp.server

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.notfallapp.MainActivity
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class ServerApi {
    companion object{
        private lateinit var context: Context
        var serverAPIURL = "https://jamesdev.ilogs.com/api/v1"
        //var serverAPIURL = "https://safemotiondev.ilogs.com/API/v1"
        const val TAG = "ServerApi"
        var volleyRequestQueue: RequestQueue? = null

        private var timeTokenCome: Long? = null

        // login response data
        var accessToken: String? = null
        private var refreshToken: String? = null
        private var multiFactorToken: String? = null
        private var tokenExpiresInSeconds: Int? = null
        private var multiFactorAuth: Boolean? = null
        private var username: String? = null
        var userId: UUID? = null


        fun setContext(context: Context){
            this.context = context
        }

        fun controlToken(){
            if(timeTokenCome!=null && tokenExpiresInSeconds!=null){
                if((timeTokenCome!! + tokenExpiresInSeconds!! - 10) < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())){
                    refreshToken()
                }
            }
        }

        fun sendLogInDataToServer(username: String, password: String){
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            // Add your parameters in HashMap
            reqBody.put("Username", username)
            reqBody.put("Password", password)
            reqBody.put("ClientId", null/*Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)*/)

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "$serverAPIURL/login", reqBody,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")

                // Handle Server response here
                try {
                    val isSuccess = response.getBoolean("isSuccess")
                    val code = response.getInt("code")
                    val message = response.getString("message")
                    if (response.has("data")) {
                        val data = response.getJSONObject("data")
                        // Handle your server response data here

                        timeTokenCome = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

                        accessToken = data.getString("AccessToken")
                        refreshToken = data.getString("RefreshToken")
                        multiFactorToken = data.getString("MultiFactorToken")
                        tokenExpiresInSeconds = data.getInt("TokenExpiresInSeconds")
                        multiFactorAuth = data.getBoolean("MultiFactorAuth")
                        this.username = data.getString("Username")
                        userId = data.get("UserId") as UUID?

                    }
                    Log.e(TAG, "Erfolgreich eingelogt")
                    Log.e(TAG, message)

                    /*val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)*/

                } catch (e: Exception) { // caught while parsing the response
                    Log.e(TAG, "problem occurred")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                val resErrorBody = JSONObject(String(error.networkResponse.data))
                Log.e(TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
            }
            )

            // Adding request to request queue
            volleyRequestQueue?.add(jsonObjectRequest)
        }

        private fun refreshToken(){
            val reqBody = JSONObject()
            reqBody.put("RefreshToken", refreshToken)
            reqBody.put("ClientId", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "$serverAPIURL/login/refreshtoken", reqBody,
                Response.Listener { response ->
                    Log.e(TAG, "response: $response")

                    try {
                        val isSuccess = response.getBoolean("isSuccess")
                        val code = response.getInt("code")
                        val message = response.getString("message")
                        if (response.has("data")) {
                            val data = response.getJSONObject("data")
                            accessToken = data.getString("AccessToken")
                            refreshToken = data.getString("RefreshToken")
                            multiFactorToken = data.getString("MultiFactorToken")
                            tokenExpiresInSeconds = data.getInt("TokenExpiresInSeconds")
                            multiFactorAuth = data.getBoolean("MultiFactorAuth")

                            timeTokenCome = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                        }
                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(TAG, "problem occurred")
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    val resErrorBody = JSONObject(String(error.networkResponse.data))
                    Log.e(TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
                }
            )
            volleyRequestQueue?.add(jsonObjectRequest)
        }

        fun createGetCall(extraUrl: String, toDo: (response: JSONObject) -> Unit ) {
            controlToken()

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.GET, serverAPIURL + extraUrl, null,
                Response.Listener<JSONObject> { response ->
                    Log.e(TAG, "response: $response")
                    try {
                        val isSuccess = response.getBoolean("isSuccess")
                        val code = response.getInt("code")
                        val message = response.getString("message")

                        toDo(response)

                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(TAG, "problem occurred")
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    val resErrorBody = JSONObject(String(error.networkResponse.data))
                    Log.e(TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String>? = super.getHeaders()
                    if (params == null) params = HashMap()
                    params["Authorization"] = accessToken.toString()
                    return params
                }
            }
            volleyRequestQueue?.add(jsonObjectRequest)
        }

        fun createCall(method: Int, extraUrl: String, reqBody: JSONObject?, toDo: (response: JSONObject) -> Unit ) {
            controlToken()

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                method, serverAPIURL + extraUrl, reqBody,
                Response.Listener<JSONObject> { response ->
                    Log.e(TAG, "response: $response")
                    try {
                        val isSuccess = response.getBoolean("isSuccess")
                        val code = response.getInt("code")
                        val message = response.getString("message")

                        toDo(response)

                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(TAG, "problem occurred")
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    val resErrorBody = JSONObject(String(error.networkResponse.data))
                    Log.e(TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String>? = super.getHeaders()
                    if (params == null) params = HashMap()
                    params["Authorization"] = accessToken.toString()
                    return params
                }
            }
            volleyRequestQueue?.add(jsonObjectRequest)
        }

    }
}