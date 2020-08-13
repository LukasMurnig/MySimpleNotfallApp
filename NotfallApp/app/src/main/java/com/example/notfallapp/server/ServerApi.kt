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

/**
 * class has the required parameters for the server, the login function, the recreateToken function and
 * the createJsonObjectRequest function, which create the basic request for almost all requests
 */
class ServerApi : ICheckPermission {
    companion object{
        private lateinit var context: Context
        private lateinit var sharedPreferences: SharedPreferences
        var serverAPIURL = "https://jamesdev.ilogs.com/api/v1"
        const val TAG = "ServerApi"
        private const val clientID = "299a645f-5fc3-48ac-8098-01baaa4c2caa"
        var volleyRequestQueue: RequestQueue? = null

        private var timeTokenCome: Long? = null

        // login response data
        var accessToken: String? = null
        private var refreshToken: String? = null
        private var multiFactorToken: String? = null
        private var tokenExpiresInSeconds: Int? = null
        private var multiFactorAuth: Boolean? = null
        private var username: String? = null
        var userId: String? = null


        /**
         * set the context of the companion object
         */
        fun setContext(context: Context){
            this.context = context
        }

        /**
         * set the shared preferences of the companion object
         */
        fun setSharedPreferences(sharedPreferences: SharedPreferences){
            this.sharedPreferences = sharedPreferences
        }

        /**
         * get the shared preferences of the companion object
         */
        fun getSharedPreferences(): SharedPreferences{
            return this.sharedPreferences
        }

        /**
         * create the request to login, handle the response and
         * when it is successful, save data to the shared preferences and opens
         * the MainActivity
         */
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
                    val accessTokenCheck = response.has("AccessToken")
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
                            println("Error :$ex")
                            LoginActivity.errorLogin.text = context.getString(R.string.unexpectedErrorLogin)
                        }

                        saveDataToSharedPreferences()

                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    Log.d(TAG, "Erfolgreich eingelogt")

                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)

                } catch (e: Exception) {
                    Log.e(TAG, "problem occurred " + e.printStackTrace())
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

        /**
         * create the request to get a new token from the server, handle the response, when
         * successful response data to the shared preferences
         */
        fun refreshToken(){
            val reqBody = JSONObject()
            val pref = getSharedPreferences()
            reqBody.put("RefreshToken", pref.getString("RefreshToken", null)/*refreshToken*/)
            reqBody.put("ClientId", clientID)

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "$serverAPIURL/login/refreshtoken", reqBody,
                Response.Listener { response ->
                    Log.i(TAG, "response: $response")

                    try {
                        val accessTokenCheck = response.has("AccessToken")
                        if (accessTokenCheck)
                        {
                            try {
                                accessToken = response.get("AccessToken") as String?
                                refreshToken = response.get("RefreshToken") as String?
                                tokenExpiresInSeconds = response.get("TokenExpiresInSeconds") as Int
                                multiFactorAuth = response.get("MultiFactorAuth") as Boolean?
                            }catch (ex: Exception){
                                println("Error :$ex")
                            }

                            saveDataToSharedPreferences()

                            timeTokenCome = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                            Log.i(TAG, "Refresh token Successfully")
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
            if(volleyRequestQueue != null){
                volleyRequestQueue!!.add(jsonObjectRequest)
            } else {
                Log.i(TAG, "konnte token nicht refreshen da volley Request Queue nicht instanziert war")
            }
        }

        // save response data to sharedPreferences
        private fun saveDataToSharedPreferences(){
            val editor = sharedPreferences.edit()
            editor.putString("AccessToken", accessToken)
            editor.putString("RefreshToken", refreshToken)
            editor.putInt("tokenExpiresInSeconds", tokenExpiresInSeconds!!)
            editor.putBoolean("MultiFactorAuth", multiFactorAuth!!)
            if(userId != null){
                editor.putString("UserId", userId)
            }
            editor.commit()

            try {
                val expires: Int = tokenExpiresInSeconds!! - 60
                val expiresTime = expires.toString().toLong()
                ICheckPermission.getNewTokenBeforeExpires(expiresTime * 1000)
            }catch(ex: java.lang.Exception){
                println(ex.toString())
            }
        }

        /**
         * create the basic request for almost all requests in the program
         */
        fun createJsonObjectRequest(method: Int, extraUrl: String, reqBody: JSONObject?, toDo: (response: JSONObject) -> Unit ) {

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                method, serverAPIURL + extraUrl, reqBody,
                Response.Listener<JSONObject> { response ->
                    Log.i(TAG, "response: $response")
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
                            if(error.message == null){
                                throw java.lang.Exception()
                            }
                            Log.e(TAG, "problem occurred, volley error: " + error.message)
                        }
                    }catch (ex: Exception){
                        Log.e(TAG, "problem occurred, volley error: " + error)
                    }
                }) {
                @Throws(AuthFailureError::class) // add the Authorization header to the server request
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String>? = super.getHeaders()
                    if (params == null || params.isEmpty() ) params = HashMap()

                    val access = getSharedPreferences().getString("AccessToken", null)
                    if(access != null){
                        params["Authorization"] = "Bearer $access"
                    }

                    return params
                }
            }
            volleyRequestQueue?.add(jsonObjectRequest)
        }
    }
}