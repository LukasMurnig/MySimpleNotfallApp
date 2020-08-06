package com.example.notfallapp.server

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.login.LoginActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class ServerApi {
    companion object{
        private lateinit var context: Context
        private lateinit var sharedPreferences: SharedPreferences
        var serverAPIURL = "https://jamesdev.ilogs.com/api/v1"
        //var serverAPIURL = "https://jamesdev.ilogs.com"
        //var serverAPIURL = "https://safemotiondev.ilogs.com/API/v1"
        const val TAG = "ServerApi"
        val clientID = "299a645f-5fc3-48ac-8098-01baaa4c2caa"
        private var volleyRequestQueue: RequestQueue? = null

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

        fun setSharedPreferences(sharedPreferences: SharedPreferences){
            this.sharedPreferences = sharedPreferences
        }

        private fun controlToken(){
            if(timeTokenCome!=null && tokenExpiresInSeconds!=null){
                if((timeTokenCome!! + tokenExpiresInSeconds!! - 10) < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())){
                    refreshToken()
                }
            }
        }

        fun sendLogInDataToServer(username: String, password: String, context: Context){
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()

            try {
                reqBody.put("Username", username)
                reqBody.put("Password", password)
                reqBody.put("ClientId", clientID)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "$serverAPIURL/login", reqBody,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")

                try {
                    //val message = response.getString("message")
                    if (response.has("data")) {
                        val data = response.getJSONObject("data")
                        // Handle your server response data here

                        timeTokenCome = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

                        accessToken = proveIfNullOrValue("AccessToken", data) as String?
                        refreshToken = proveIfNullOrValue("RefreshToken", data) as String?
                        multiFactorToken = proveIfNullOrValue("MultiFactorToken", data) as String?
                        tokenExpiresInSeconds = proveIfNullOrValue("TokenExpiresInSeconds", data) as Int?
                        multiFactorAuth = proveIfNullOrValue("MultiFactorAuth", data) as Boolean?
                        this.username = proveIfNullOrValue("Username", data) as String?
                        userId = proveIfNullOrValue("UserId", data) as UUID?
                        accessToken = data.getString("AccessToken")
                        refreshToken = data.getString("RefreshToken")
                        multiFactorToken = data.getString("MultiFactorToken")
                        tokenExpiresInSeconds = data.getInt("TokenExpiresInSeconds")
                        multiFactorAuth = data.getBoolean("MultiFactorAuth")
                        this.username = data.getString("Username")
                        userId = data.get("UserId") as UUID?
                        var editor = sharedPreferences.edit()
                        editor.putString("clientID", clientID)
                        editor.putString("accessToken", accessToken)
                        editor.putString("refreshToken", refreshToken)
                        editor.putString("multiFactorToken", multiFactorToken)
                        editor.putInt("tokenExpiresInSeconds", tokenExpiresInSeconds!!)
                        editor.putBoolean("multiFactorAuth", multiFactorAuth!!)
                        editor.putString("UserId", userId.toString())
                        editor.commit()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    Log.e(TAG, "Erfolgreich eingelogt")
                    //Log.e(TAG, message)

                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)

                } catch (e: Exception) {
                    Log.e(TAG, "problem occurred")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                if(error.networkResponse != null){
                    val resErrorBody = JSONObject(String(error.networkResponse.data))
                    LoginActivity.errorLogin.text = context.getString(R.string.forbidden)
                    Log.e(TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
                }else{
                    LoginActivity.errorLogin.text = context.getString(R.string.internError)
                    Log.e(TAG, "problem occurred, volley error: " + error.message)
                }
            })
            volleyRequestQueue?.add(jsonObjectRequest)
        }

        private fun proveIfNullOrValue(key: String, response: JSONObject): Any?{
            return try{
                response.get(key)
            }catch (ex: JSONException){
                null
            }
        }

        fun refreshToken(){
            val reqBody = JSONObject()
            reqBody.put("RefreshToken", refreshToken)
            reqBody.put("ClientId", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "$serverAPIURL/login/refreshtoken", reqBody,
                Response.Listener { response ->
                    Log.e(TAG, "response: $response")

                    try {
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

        fun createCall(method: Int, extraUrl: String, reqBody: JSONObject?, toDo: (response: JSONObject) -> Unit ) {
            controlToken()

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                method, serverAPIURL + extraUrl, reqBody,
                Response.Listener<JSONObject> { response ->
                    Log.e(TAG, "response: $response")
                    try {
                        val code = response.getInt("code")
                        val message = response.getString("message")

                        Log.i(TAG, "$code  $message")

                        toDo(response)

                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(TAG, "problem occurred")
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    if(error.networkResponse == null){
                        val resErrorBody = JSONObject(String(error.networkResponse.data))
                        Log.e(TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
                    }else{
                        Log.e(TAG, "problem occurred, volley error: " + error.message)
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String>? = super.getHeaders()
                    if (params == null) params = HashMap()
                    if(accessToken != null){
                        params["Authorization"] = accessToken.toString()
                    }
                    return params
                }
            }
            volleyRequestQueue?.add(jsonObjectRequest)
        }
    }
}