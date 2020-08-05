package com.example.notfallapp.server

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.alarm.AlarmSuccesfulActivity
import com.example.notfallapp.interfaces.IConnectBracelet
import com.example.notfallapp.login.LoginActivity
import com.example.notfallapp.service.ServiceCallAlarm
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class ServerCallAlarm {
    companion object {
        private lateinit var sharedPreferences: SharedPreferences
        var serverAPIURL = "https://jamesdev.ilogs.com/api/v1"

        //var serverAPIURL = "https://safemotiondev.ilogs.com/API/v1"
        const val TAG = "ServerApi"
        private var volleyRequestQueue: RequestQueue? = null
        var userId: String? = null

        fun setSharedPreferences(sharedPreferences: SharedPreferences) {
            this.sharedPreferences = sharedPreferences
        }

        private fun sendAlarm(context: Context) {
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            reqBody.put("Type", 0)
            if (IConnectBracelet.batteryState.equals(" ")) {
                reqBody.put("Battery", null)
            } else {
                reqBody.put("Battery", IConnectBracelet.batteryState)
            }
            userId = sharedPreferences.getString("UserId", "")
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "${ServerApi.serverAPIURL}/users/${userId}/alert", reqBody,
                Response.Listener { response ->
                    Log.e(ServerApi.TAG, "response: $response")
                    var intent = Intent(context, AlarmSuccesfulActivity::class.java)
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
                        var intent = Intent(context, ServiceCallAlarm::class.java)
                        context.startService(intent)
                    } else {
                        Log.e(ServerApi.TAG, "problem occurred, volley error: " + error.message)
                        var intent = Intent(context, ServiceCallAlarm::class.java)
                        context.startService(intent)
                    }
                })
            volleyRequestQueue?.add(jsonObjectRequest)
        }
    }
}