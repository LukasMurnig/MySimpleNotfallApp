package com.example.notfallapp.server

import android.content.Context
import android.net.ConnectivityManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.example.notfallapp.R
import com.example.notfallapp.adapter.AlertsListAdapter
import com.example.notfallapp.bll.Alert
import com.example.notfallapp.interfaces.CurrentLocation
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class ServerAlarm {
    fun getAllAlerts(context: Context, rvAlarms: RecyclerView, lbMessageNoAlarms: TextView){
        ServerApi.createCall(Request.Method.GET, "/alerts", null) { response ->
            if (response.has("data")) {
                var data: JSONArray? = null
                try{
                    data = response.getJSONArray("data")
                }catch (ex: JSONException){

                }

                if(data == null){
                    lbMessageNoAlarms.text = context.resources.getString(R.string.noAlarms)
                }else{
                    val result = mutableListOf<Alert>()

                    for (i in 0 until data.length()) {
                        val js = data.getJSONObject(i)
                        result.plus(Alert(
                            js.get("ID") as Long,
                            js.get("Date") as Date,
                            js.get("Type") as Byte,
                            js.get("State") as Byte,
                            js.get("ClientId") as UUID,
                            proveIfNullOrValue("HelperId", js) as UUID?,
                            proveIfNullOrValue("DeviceId", js) as UUID?,
                            proveIfNullOrValue("TriggeringPositionLatitude", js) as Double?,
                            proveIfNullOrValue("TriggeringPositionLongitude", js) as Double?,
                            proveIfNullOrValue("TriggeringPositionTime", js) as Date?,
                            js.get("CanBeForwarded") as Boolean
                        ))
                    }
                    val adapter = AlertsListAdapter(result)
                    rvAlarms.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
        }
        // Solange Server noch nicht funktioniert; Keine genaue Beschreibung über den Response List of Alert-ObjectsList
        var result = listOf<Alert>()
        val jsonArray = JSONArray()
        val json = JSONObject()
        json.put("ID", 390144198431043)
        json.put("Date", Date())
        json.put("Type", (0).toByte())
        json.put("State", (0).toByte())
        json.put("ClientId", UUID(230943,3204))
        json.put("HelperId", UUID(2094582,4595))
        json.put("DeviceId", null)
        json.put("TriggeringPositionLatitude", 22.222222)
        json.put("TriggeringPositionLongitude", 22.222222)
        json.put("TriggeringPositionTime", Date())
        json.put("CanBeForwarded", false)
        jsonArray.put(json)

        val endJson = JSONObject()
        endJson.put("data", jsonArray)

        val data: JSONArray = endJson.getJSONArray("data")

        for (i in 0 until data.length()) {
            val js = data.getJSONObject(i)
            result = result.plus(Alert(
                js.get("ID") as Long,
                js.get("Date") as Date,
                js.get("Type") as Byte,
                js.get("State") as Byte,
                js.get("ClientId") as UUID,
                proveIfNullOrValue("HelperId", js) as UUID?,
                proveIfNullOrValue("DeviceId", js) as UUID?,
                proveIfNullOrValue("TriggeringPositionLatitude", js) as Double?,
                proveIfNullOrValue("TriggeringPositionLongitude", js) as Double?,
                proveIfNullOrValue("TriggeringPositionTime", js) as Date?,
                js.get("CanBeForwarded") as Boolean
            ))
        }

        val adapter = AlertsListAdapter(result)
        rvAlarms.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun proveIfNullOrValue(key: String, response: JSONObject): Any?{
        return try{
            response.get(key)
        }catch (ex: JSONException){
            null
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

        val location = CurrentLocation.getCurrentLocation(context)
        val positions = JSONObject()
        val timestamp = Calendar.getInstance().time
        if(location != null){
            positions.put("Timestamp", timestamp)
            positions.put("Longitude", location.longitude)
            positions.put("Latitude", location.latitude)
            positions.put("Accuracy", location.accuracy)
            positions.put("Source", location.provider)
        }

        reqBody.put("Positions", positions)

        val beacons = JSONObject()
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        val type: Int?
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
}