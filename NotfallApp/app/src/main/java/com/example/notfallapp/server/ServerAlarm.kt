package com.example.notfallapp.server

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
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
    fun getAllAlerts(rvAlarms: RecyclerView, lbMessageNoAlarms: TextView){
        ServerApi.createCall(Request.Method.GET, "/alerts", null) { response ->
            if (response.has("data")) {
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
                    val adapter = AlertsListAdapter(result)
                    rvAlarms.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun sendAlert(){
        val reqBody = JSONObject()
        // type from alert model: 0 -> SOS
        reqBody.put("Type", 0)
        reqBody.put("Battery", null)

        ServerApi.createCall(Request.Method.POST, "/users/${ServerApi.userId}/alert", reqBody) { response ->
            if (response.has("data")) {
                val data = response.get("data")

            }
        }
    }

    fun sendPosition(context: Context){
        val reqBody = JSONObject()

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val positions = JSONObject()
        val timestamp = Calendar.getInstance().time
        positions.put("Timestamp", timestamp)
        positions.put("Longitude", location.longitude)
        positions.put("Latitude", location.latitude)
        positions.put("Accuracy", location.accuracy)
        positions.put("Source", location.provider)
        reqBody.put("Positions", positions)

        val beacons = JSONObject()
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        var type: Int?
        type = when(networkInfo.typeName.toUpperCase(Locale.ROOT)) {
            "WIFI" -> 0
            "BLUETOOTH" -> 1
            else -> 0
        }
        beacons.put("Timestamp", timestamp)
        beacons.put("Type", type)
        beacons.put("Identifier", null)
        beacons.put("Mac", null)
        beacons.put("SignalStrength", null)
        reqBody.put("Beacons", beacons)

        ServerApi.createCall(Request.Method.POST, "/users/${ServerApi.userId}/positions", reqBody) { response ->
        }
    }

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