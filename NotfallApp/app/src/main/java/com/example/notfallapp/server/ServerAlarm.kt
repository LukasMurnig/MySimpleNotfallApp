package com.example.notfallapp.server

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.notfallapp.R
import com.example.notfallapp.adapter.AlertsListAdapter
import com.example.notfallapp.bll.Alert
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class ServerAlarm {
    fun getAllAlerts(context: Context, rvAlarms: RecyclerView, lbMessageNoAlarms: TextView){



        createGetArrayCall(Request.Method.GET, "/alerts") { response ->

                if(response.length() == 0){
                    lbMessageNoAlarms.text = context.resources.getString(R.string.noAlarms)
                }else{
                    val result = mutableListOf<Alert>()

                    for (i in 0 until response.length()) {
                        val js = response.getJSONObject(i)

                        result.add(Alert(
                            (js.get("ID") as Int).toLong(),
                            js.get("Date") as String,
                            (js.get("Type") as Int).toByte(),
                            (js.get("State") as Int).toByte(),
                            UUID.fromString(isStringOrNull("ClientId", js)),
                            convertToUUID(isStringOrNull("HelperId", js)),
                            convertToUUID(isStringOrNull("DeviceId", js)),
                            isDoubleOrNull("TriggeringPositionLatitude", js),
                            isDoubleOrNull("TriggeringPositionLongitude", js),
                            isStringOrNull("TriggeringPositionTime", js),
                            js.get("CanBeForwarded") as Boolean
                        ))
                    }
                    val adapter = AlertsListAdapter(result)
                    rvAlarms.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
        }
    }

    private fun convertToUUID(string: String?): UUID? {
        return if(string == null || string.isEmpty() || string == "null"){
            null
        } else {
            UUID.fromString(string)
        }
    }

    private fun isDoubleOrNull(key: String, response: JSONObject): Double? {
        return try{
            if(response.get(key) == null || response.get(key).equals("null")){
                null
            } else if(response.getDouble(key) == null){
                null
            } else {
                response.getDouble(key)
            }
        }catch (ex: JSONException){
            null
        }
    }

    private fun isStringOrNull(key: String, response: JSONObject): String? {
        return if(response.getString(key) == null){
            null
        } else {
            response.getString(key)
        }
    }

    fun createGetArrayCall(method: Int, extraUrl: String, toDo: (response: JSONArray) -> Unit ) {
        val jsonObjectRequest: JsonArrayRequest = object : JsonArrayRequest(
            method, ServerApi.serverAPIURL + extraUrl, null,
            Response.Listener<JSONArray> { response ->
                Log.e(ServerApi.TAG, "response: $response")
                try {
                    toDo(response)
                } catch (e: Exception) { // caught while parsing the response
                    Log.e(ServerApi.TAG, "problem occurred, while do something with response function")
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                try{
                    if(error.networkResponse == null){
                        val resErrorBody = JSONObject(String(error.networkResponse.data))
                        Log.e(ServerApi.TAG, "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get("Error"))
                    }else{
                        Log.e(ServerApi.TAG, "problem occurred, volley error: " + error.message)
                    }
                }catch (ex: Exception){
                    Log.e(ServerApi.TAG, "problem occurred, volley error: " + error)
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String>? = super.getHeaders()
                if (params == null || params.isEmpty() ) params = HashMap()

                val access = ServerApi.getSharedPreferences().getString("AccessToken", null)
                if(access != null){
                    params["Authorization"] = "Bearer $access"
                }

                return params
            }
        }
        ServerApi.volleyRequestQueue?.add(jsonObjectRequest)
    }
}