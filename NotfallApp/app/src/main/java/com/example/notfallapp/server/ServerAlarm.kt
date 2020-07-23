package com.example.notfallapp.server

import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.notfallapp.adapter.AlertsListAdapter
import com.example.notfallapp.bll.Alert
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class ServerAlarm {
    companion object{
        fun getAllAlerts(rvAlarms: RecyclerView, lbMessageNoAlarms: TextView){
            ServerApi.controlToken()

            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, ServerApi.serverAPIURL+"/alerts", null,
                Response.Listener<JSONObject> {response ->
                    Log.e(ServerApi.TAG, "response: $response")
                    try {
                        val isSuccess = response.getBoolean("isSuccess")
                        val code = response.getInt("code")
                        val message = response.getString("message")
                        if (response.has("data")) {
                            // array of json?
                            val data = response.get("data") as Array<JSONObject>
                            if(data.isEmpty()){
                                //lbMessageNoAlarms.text = resources.getString(R.string.noAlarms)
                            }else{
                                val result: List<Alert> = mutableListOf()
                                for(json: JSONObject in data){
                                    result.plus(Alert(
                                        json.get("ID") as Long,
                                        json.get("Date") as Date,
                                        json.get("Type") as Byte,
                                        json.get("State") as Byte,
                                        json.get("ClientId") as UUID,
                                        json.get("HelperId") as UUID?,
                                        json.get("DeviceId") as UUID?,
                                        json.get("TriggeringPositionLatitude") as Double?,
                                        json.get("TriggeringPositionLongitude") as Double?,
                                        json.get("TriggeringPositionTime") as Date?,
                                        json.get("CanBeForwarded") as Boolean
                                    ))
                                }
                                /*val adapter = AlertsListAdapter(result)
                                rvAlarms.adapter = adapter
                                adapter.notifyDataSetChanged()*/
                            }

                        }
                    } catch (e: Exception) { // caught while parsing the response
                        Log.e(ServerApi.TAG, "problem occurred")
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {error ->
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