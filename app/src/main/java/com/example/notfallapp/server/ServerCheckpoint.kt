package com.example.notfallapp.server

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ServerCheckpoint {
    companion object {
        private lateinit var sharedPreferences: SharedPreferences
        private const val TAG = "ServerApi"
        private var volleyRequestQueue: RequestQueue? = null
        var userId: String? = null

        fun checkLocationBluetoothBeacon(context: Context) {
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            TODO("Not yet implemented")
        }

        fun checkLocationWifiBeacon(context: Context) {
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            TODO("Not yet implemented")
        }

        fun checkLocationGeoZone(context: Context) {
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            TODO("Not yet implemented")
        }
    }
}