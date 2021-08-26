package com.example.notfallapp.server

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.notfallapp.broadcastReciever.ActionsBracelet
import com.example.notfallapp.MainActivity
import com.example.notfallapp.alarm.AlarmFailedActivity
import com.example.notfallapp.alarm.AlarmSuccessfulActivity
import com.example.notfallapp.interfaces.BeaconInRange
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.interfaces.WifiInRange
import com.example.notfallapp.login.LoginActivity
import com.example.notfallapp.server.ServerApi.Companion.createJsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.sql.Timestamp
import java.text.ParseException
import java.util.*


/**
 * class that has the function to send a alarm and the position to the server
 */
class ServerCallAlarm {
    companion object {
        private var sharedPreferences: SharedPreferences? = LoginActivity.sharedPreferences
        const val TAG = "ServerApi"
        private var sendBluetoothBeacons: Boolean = false
        private var sendWifiBeacons: Boolean = false
        private var volleyRequestQueue: RequestQueue? = null
        var userId: String? = null
        private var alarmSuccessful = false
        private var positionSuccessful = false
        var statusCode: Int? = null
        var responseMessage: String? = null

        /**
         * function send a alarm to the server and handle the response
         */
        fun sendAlarm(context: Context) {

            if (!AlarmSuccessfulActivity.isActive) {
                try {
                    volleyRequestQueue = Volley.newRequestQueue(context)
                    val reqBody = JSONObject()
                    reqBody.put("Type", 0)
                    if (ActionsBracelet.batteryState == 0) {
                        reqBody.put("Battery", null)
                    } else {
                        reqBody.put("Battery", ActionsBracelet.batteryState)
                    }
                    sharedPreferences = LoginActivity.sharedPreferences!!
                    userId = sharedPreferences!!.getString("UserId", "")
                    createJsonObjectRequest(
                        Request.Method.POST,
                        "/users/$userId/alert",
                        reqBody
                    ) { response ->
                        Log.e(ServerApi.TAG, "response Alarm: $response")
                        alarmSuccessful = true
                        ServerAlarm().getActiveAlarm(context)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         * function send the position of the user to the server and handle the response
         */
        fun sendPosition(context: Context) {
            if (!AlarmSuccessfulActivity.isActive) {
                volleyRequestQueue = Volley.newRequestQueue(context)
                val reqBody = JSONObject()
                val currentTime = createCurrentTimeStamp()
                reqBody.put("Positions", prepareGPSRequest(currentTime))
                reqBody.put("Beacons", prepareBeaconsRequest(currentTime, 2))
                Log.e("TAG", reqBody.toString())
                sharedPreferences = LoginActivity.sharedPreferences!!
                userId = sharedPreferences?.getString("UserId", "")

                createStringRequest(context, "/users/${userId}/positions", reqBody) { response ->
                    var statusCode = 0
                    try {
                        if (response != null) {
                            statusCode = response.toInt()
                        }
                    } catch (ex: ParseException) {
                        Log.e(TAG, ex.toString())
                    }
                    if (statusCode in 200..299) {
                        positionSuccessful = true
                    }
                    return@createStringRequest statusCode
                }
            }
        }

        fun prepareBeaconsRequest(currentTime: String, case: Int): JSONArray {
            val arrayBodyBeacon = JSONArray()
            var beaconBody = JSONObject()
            when (case) {
                0 -> sendBluetoothBeacons = true
                1 -> sendWifiBeacons = true
                2 -> {
                    sendBluetoothBeacons = true
                    sendWifiBeacons = true
                }
            }
            if (sendBluetoothBeacons) {
                for (indx in BeaconInRange.beacons.indices) {
                    var beacon = BeaconInRange.beacons.get(indx)
                    beaconBody = JSONObject()
                    beaconBody.put("Timestamp", currentTime)
                    beaconBody.put("Type", 2)
                    beaconBody.put("Identifier", beacon!!.id1)
                    beaconBody.put("Mac", beacon.bluetoothAddress)
                    beaconBody.put("SignalStrength", beacon.distance)
                    arrayBodyBeacon.put(beaconBody)
                }
                Log.e("ArrayBody", arrayBodyBeacon.toString())
                sendBluetoothBeacons = false
            }

            if (sendWifiBeacons) {
                for (indx in WifiInRange.wifiBeacon.indices) {
                    var beacon = WifiInRange.wifiBeacon.get(indx)
                    beaconBody = JSONObject()
                    beaconBody.put("Timestamp", currentTime)
                    beaconBody.put("Type", 1)
                    beaconBody.put("Identifier", beacon.SSID)
                    beaconBody.put("SignalStrength", beacon!!.level)
                    arrayBodyBeacon.put(beaconBody)
                }
                Log.e("ArrayBody", arrayBodyBeacon.toString())
                sendWifiBeacons = false
            }
            return arrayBodyBeacon
        }

        fun prepareGPSRequest(currentTime: String): JSONArray {
            val body = JSONObject()
            val arrayBody = JSONArray()
            val location = CurrentLocation.currentLocation
            body.put("Timestamp", currentTime)

            if (location?.longitude != null) {
                body.put("Longitude", location.longitude)
            } else {
                body.put("Longitude", 0)
            }

            if (location?.latitude != null) {
                body.put("Latitude", location.latitude)
            } else {
                body.put("Latitude", 0)
            }

            if (location?.accuracy != null) {
                body.put("Accuracy", location.accuracy)
            } else {
                body.put("Accuracy", 0)
            }

            body.put("Source", "gps")
            arrayBody.put(body)
            return arrayBody
        }

        fun createCurrentTimeStamp(): String {
            val time =
                Timestamp(System.currentTimeMillis() - 7200000).toString() // remove 2 hours on the timestamp
            val times = time.split(" ")
            return times[0] + "T" + times[1] + "+00:00"
        }

        /**
         * function stops the alarm
         */
        fun stopAlarm(context: Context, idAlarm: String) {
            createJsonObjectRequest(
                Request.Method.POST,
                "/alerts/$idAlarm/close",
                null
            ) { response ->
                Log.i(TAG, "Stopped the alarm")
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }

        /**
         * function creates the String request for the two functions above
         */
        fun createStringRequest(
            context: Context,
            url: String,
            reqBody: JSONObject,
            response: (String?) -> Int
        ) {
            val requestBody = reqBody.toString()
            val stringRequest = object : StringRequest(
                Method.POST, "${ServerApi.serverAPIURL}$url",
                Response.Listener { response ->
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
                    if (url == "alert") {
                        val intent = Intent(context, AlarmFailedActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                }) {
                override fun getHeaders(): Map<String, String>? {
                    val params = HashMap<String, String>()
                    val token = sharedPreferences?.getString("AccessToken", "")
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
                        if (requestBody == null) {
                            null
                        } else {
                            requestBody.toByteArray(Charsets.UTF_8)
                        }
                    } catch (ex: UnsupportedEncodingException) {
                        VolleyLog.wtf(
                            "Unsupported Encoding while trying to get the bytes of %s using %s",
                            reqBody,
                            "utf-8"
                        )
                        null
                    }
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    var responseString = ""
                    if (response != null) {
                        var statusCode = response.statusCode as String
                        var message = response.data as String
                        responseString = java.lang.String.valueOf(response)
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

        fun createJSONRequest(context: Context, url: String, reqBody: JSONObject, type: Int) {
            val requestBody = reqBody.toString()
            volleyRequestQueue = Volley.newRequestQueue(context)
            val token = sharedPreferences?.getString("AccessToken", "")
            Log.e("Body",token)
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.POST, "${ServerApi.serverAPIURL}$url", reqBody,
                { response ->
                    Log.e("Response", response.toString())
                    ServerResponse.recievedServerResponse(response, null, type)
                },
                { error ->
                    Log.e("Response-Error", error.toString())
                    var response = error.networkResponse
                    if (response!= null) ServerResponse.recievedServerResponse(null, response, type)
                    else{
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
                    if (url == "alert") {
                        val intent = Intent(context, AlarmFailedActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                }}) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    var params: HashMap<String, String>? = null
                    if (params == null) params = HashMap(super.getHeaders())
                    val token = sharedPreferences?.getString("AccessToken", "")
                    params["Authorization"] = "Bearer $token"
                    return params
                }
            }
            volleyRequestQueue?.add(jsonObjectRequest)
        }
    }
}