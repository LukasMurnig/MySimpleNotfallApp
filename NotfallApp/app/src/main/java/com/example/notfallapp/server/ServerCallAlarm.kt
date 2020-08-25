package com.example.notfallapp.server

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.notfallapp.BroadcastReciever.ActionsBracelet
import com.example.notfallapp.MainActivity
import com.example.notfallapp.alarm.AlarmFailedActivity
import com.example.notfallapp.interfaces.BeaconInRange
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.login.LoginActivity
import com.example.notfallapp.server.ServerApi.Companion.createJsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.sql.Timestamp
import java.text.ParseException
import java.util.*


/**
 * class that has the function to send a alarm and the position to the server
 */
class ServerCallAlarm {
    companion object {
        private lateinit var sharedPreferences: SharedPreferences
        private const val TAG = "ServerApi"
        private var volleyRequestQueue: RequestQueue? = null
        var userId: String? = null
        private var alarmSuccessful = false
        private var positionSuccessful = false

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

            createJsonObjectRequest(Request.Method.POST, "/users/$userId/alert", reqBody){ response ->
                Log.e(ServerApi.TAG, "response Alarm: $response")
                    alarmSuccessful = true
                    ServerAlarm().getActiveAlarm(context)
                }
        }

        /**
         * function send the position of the user to the server and handle the response
         */
        fun sendPosition(context: Context){
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            val body = JSONObject()
            val beaconBody = JSONObject()
            val arrayBody = JSONArray()
            val arrayBodyBeacon = JSONArray()
            val time = Timestamp(System.currentTimeMillis()).toString()
            val times = time.split(" ")
            val currentTime = times[0]+"T"+times[1]+"+00:00"
            val location = CurrentLocation.currentLocation
            val beacons = BeaconInRange.beacons
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
            if(beacons != null) {
                for (indx in beacons.indices) {
                    var beacon = beacons.get(indx)
                    beaconBody.put("Timestamp", currentTime)
                    beaconBody.put("Type", beacon!!.beaconTypeCode)
                    beaconBody.put("Identifier", beacon.id1)
                    beaconBody.put("Mac", beacon.bluetoothAddress)
                    beaconBody.put("SignalStrength", beacon.distance)
                    arrayBodyBeacon.put(beaconBody)
                }
                Log.e("ArrayBody",arrayBodyBeacon.toString())
            }
            reqBody.put("Positions", arrayBody)
            reqBody.put("Beacon", arrayBodyBeacon)
            Log.e("TAG", reqBody.toString())
            sharedPreferences = LoginActivity.sharedPreferences!!
            userId = sharedPreferences.getString("UserId", "")

            createStringRequest(context, "positions", reqBody){response ->
                var statusCode = 0
                try{
                    statusCode = response.toInt()
                }catch(ex: ParseException){
                    Log.e(TAG, ex.toString())
                }
                if (statusCode in 200..299){
                    positionSuccessful = true
                }
            }
        }

        /**
         * function stops the alarm
         */
        fun stopAlarm(context: Context, idAlarm: String){
            createJsonObjectRequest(Request.Method.POST, "/alerts/$idAlarm/close", null){response ->
                Log.i(TAG, "Stopped the alarm")
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }

        /**
         * function creates the String request for the two functions above
         */
        private fun createStringRequest(context: Context, url: String, reqBody: JSONObject, response: (response: String) -> Unit){
            val requestBody = reqBody.toString()

            val stringRequest = object : StringRequest(
                Method.POST, "${ServerApi.serverAPIURL}/users/${userId}/$url",
                Response.Listener<String> { response ->
                    response(response)
                },
                Response.ErrorListener { error ->
                    if (error.networkResponse != null) {
                        val resErrorBody = JSONObject(String(error.networkResponse.data))
                        if (resErrorBody.get("Error") != "org.json.JSONException: End of input at character 0 of ") {
                            Log.e(
                                ServerApi.TAG,
                                "problem occurred, volley error: " + error.networkResponse.statusCode + " " + resErrorBody.get(
                                    "Error"
                                )
                            )
                        }
                    } else {
                        Log.e(ServerApi.TAG, "problem occurred, volley error: " + error.message)
                    }
                    if(url == "alert"){
                        val intent = Intent(context, AlarmFailedActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                }) {
                override fun getHeaders(): Map<String, String>? {
                    val params = HashMap<String, String>()
                    val token = sharedPreferences.getString("AccessToken", "")
                    params["Authorization"] = "Bearer $token"
                    //..add other headers
                    return params
                }
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray? {
                    return try {
                        if(requestBody == null){
                            null
                        }else{
                            requestBody.toByteArray(Charsets.UTF_8)
                        }
                    } catch (ex: UnsupportedEncodingException) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", reqBody, "utf-8")
                        null
                    }
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    var responseString = ""
                    if (response != null) {
                        responseString = java.lang.String.valueOf(response.statusCode)
                        // can get more details such as response.headers
                    }
                    return Response.success(
                        responseString,
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                }
            }
            volleyRequestQueue?.add(stringRequest)
        }
    }
}