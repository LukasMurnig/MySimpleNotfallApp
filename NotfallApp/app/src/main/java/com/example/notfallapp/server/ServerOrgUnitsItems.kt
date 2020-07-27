package com.example.notfallapp.server

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.notfallapp.menubar.contact.AddContactActivity
import org.json.JSONObject

class ServerOrgUnitsItems {

    fun getOrgUnitItems(){
        ServerApi.createCall(Request.Method.GET, "/orgunits/${ServerApi.userId}/items", null) { response ->
            if (response.has("data")) {
                val data = response.getJSONObject("data")

                val phoneAreaCodes = data.getJSONArray("PhoneAreaCodes")
                for(i in 0 until phoneAreaCodes.length()){
                    val item = phoneAreaCodes.getJSONObject(i)
                    AddContactActivity.phoneAreaCodes?.plus(Pair(item.getString("Key"),item.getString("Value")))
                }

                val timezones = data.getJSONArray("Timezones")
                for(i in 0 until timezones.length()){
                    val item = timezones.getJSONObject(i)
                    AddContactActivity.timezones?.plus(Pair(item.getString("Key"),item.getString("Value")))
                }

                val countries = data.getJSONArray("Countries")
                for(i in 0 until countries.length()){
                    val item = countries.getJSONObject(i)
                    AddContactActivity.countries?.plus(Pair(item.getString("Key"),item.getString("Value")))
                }

                val languages = data.getJSONArray("Countries")
                for(i in 0 until languages.length()){
                    val item = languages.getJSONObject(i)
                    AddContactActivity.languages?.plus(Pair(item.getString("Key"),item.getString("Value")))
                }
            }
        }
    }

    companion object{
        fun getOrgUnitItems(){
            ServerApi.controlToken()

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                // id of a organization
                Method.GET, ServerApi.serverAPIURL+"/orgunits/${ServerApi.userId}/items", null,
                Response.Listener<JSONObject> { response ->
                    Log.e(ServerApi.TAG, "response: $response")
                    try {
                        val isSuccess = response.getBoolean("isSuccess")
                        val code = response.getInt("code")
                        val message = response.getString("message")
                        if (response.has("data")) {
                            val data = response.getJSONObject("data")

                            val phoneAreaCodes = data.getJSONArray("PhoneAreaCodes")
                            for(i in 0 until phoneAreaCodes.length()){
                                val item = phoneAreaCodes.getJSONObject(i)
                                AddContactActivity.phoneAreaCodes?.plus(Pair(item.getString("Key"),item.getString("Value")))
                            }

                            val timezones = data.getJSONArray("Timezones")
                            for(i in 0 until timezones.length()){
                                val item = timezones.getJSONObject(i)
                                AddContactActivity.timezones?.plus(Pair(item.getString("Key"),item.getString("Value")))
                            }

                            val countries = data.getJSONArray("Countries")
                            for(i in 0 until countries.length()){
                                val item = countries.getJSONObject(i)
                                AddContactActivity.countries?.plus(Pair(item.getString("Key"),item.getString("Value")))
                            }

                            val languages = data.getJSONArray("Countries")
                            for(i in 0 until languages.length()){
                                val item = languages.getJSONObject(i)
                                AddContactActivity.languages?.plus(Pair(item.getString("Key"),item.getString("Value")))
                            }
                        }
                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(ServerApi.TAG, "problem occurred")
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    val resErrorBody = JSONObject(String(error.networkResponse.data))
                    Log.e(ServerApi.TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String>? = super.getHeaders()
                    if (params == null) params = HashMap()
                    params["Authorization"] = ServerApi.accessToken.toString()
                    //..add other headers
                    return params
                }
            }

            ServerApi.volleyRequestQueue?.add(jsonObjectRequest)
        }
    }
}