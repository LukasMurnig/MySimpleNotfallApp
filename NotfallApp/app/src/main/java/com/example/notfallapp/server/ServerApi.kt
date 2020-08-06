package com.example.notfallapp.server

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.login.LoginActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class ServerApi : ICheckPermission {
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
        var userId: String? = null


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
                    var accessTokenCheck = response.has("AccessToken")
                    if (accessTokenCheck)
                    {
                        timeTokenCome = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                        try {
                            accessToken = response.get("AccessToken") as String?
                            refreshToken = response.get("RefreshToken") as String?
                            tokenExpiresInSeconds = response.get("TokenExpiresInSeconds") as Int
                            multiFactorAuth = response.get("MultiFactorAuth") as Boolean?
                            this.username = response.get("Username") as String?
                            userId = response.get("UserId") as String?
                        }catch (ex: Exception){
                            println("Error :" + ex.toString())
                            LoginActivity.errorLogin.text = context.getString(R.string.unexpectedErrorLogin)
                        }

                        var editor = sharedPreferences.edit()
                        editor.putString("clientID", clientID)
                        editor.putString("AccessToken", accessToken)
                        editor.putString("RefreshToken", refreshToken)
                        editor.putInt("tokenExpiresInSeconds", tokenExpiresInSeconds!!)
                        editor.putBoolean("MultiFactorAuth", multiFactorAuth!!)
                        editor.putString("UserId", userId)
                        editor.commit()
                        try {
                            var expires: Int = tokenExpiresInSeconds!! - 60
                            var expiresTime = expires.toString().toLong()
                            ICheckPermission.getNewTokenBeforeExpires(expiresTime)
                        }catch(ex : java.lang.Exception){
                            println(ex.toString())
                        }
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    Log.d(TAG, "Erfolgreich eingelogt")
                    Log.d(TAG, "userId: $userId")

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
            reqBody.put("ClientId", clientID)

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "$serverAPIURL/login/refreshtoken", reqBody,
                Response.Listener { response ->
                    Log.e(TAG, "response: $response")

                    try {
                        var accessTokenCheck = response.has("AccessToken")
                        if (accessTokenCheck)
                        {
                            try {
                                accessToken = response.get("AccessToken") as String?
                                refreshToken = response.get("RefreshToken") as String?
                                tokenExpiresInSeconds = response.get("TokenExpiresInSeconds") as Int
                                multiFactorAuth = response.get("MultiFactorAuth") as Boolean?
                                this.username = response.get("Username") as String?
                                userId = response.get("UserId") as String?
                            }catch (ex: Exception){
                                println("Error :" + ex.toString())
                            }
                            var editor = sharedPreferences.edit()
                            editor.putString("AccessToken", accessToken)
                            editor.putString("RefreshToken", refreshToken)
                            editor.putString("multiFactorToken", multiFactorToken)
                            editor.putInt("tokenExpiresInSeconds", tokenExpiresInSeconds!!)
                            editor.putBoolean("MultiFactorAuth", multiFactorAuth!!)
                            try {
                                var expires: Int = tokenExpiresInSeconds!! - 60
                                var expiresTime = expires.toString().toLong()
                                ICheckPermission.getNewTokenBeforeExpires(expiresTime)
                            }catch(ex : java.lang.Exception){
                                println(ex.toString())
                            }
                            timeTokenCome = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                            Log.e(TAG, "Refresh Successfully")
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
            //controlToken()

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                method, serverAPIURL + extraUrl, reqBody,
                Response.Listener<JSONObject> { response ->
                    Log.e(TAG, "response: $response")
                    try {
                        toDo(response)
                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(TAG, "problem occurred, while do something with response function")
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    try{
                        if(error.networkResponse == null){
                            val resErrorBody = JSONObject(String(error.networkResponse.data))
                            Log.e(TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
                        }else{
                            Log.e(TAG, "problem occurred, volley error: " + error.message)
                        }
                    }catch (ex: Exception){
                        Log.e(TAG, "problem occurred, volley error: " + error)
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String>? = super.getHeaders()
                    if (params == null || params.isEmpty() ) params = HashMap()
                    if(accessToken != null){
                        //params.put("Authorization", accessToken.toString())
                        Log.d(TAG, params.toString())
                        params["Authorization"] = "Bearer " + accessToken.toString()
                    }
                    return params
                }
            }
            volleyRequestQueue?.add(jsonObjectRequest)
        }
    }
}