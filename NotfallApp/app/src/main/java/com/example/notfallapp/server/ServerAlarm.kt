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
import org.json.JSONObject
import java.util.*


class ServerAlarm {
    fun getAllAlerts(context: Context, rvAlarms: RecyclerView, lbMessageNoAlarms: TextView){
        ServerApi.createCall(Request.Method.GET, "/alerts", null) { response ->
            if (response.has("data")) {
                val data = response.get("data") as Array<JSONObject>
                if(data.isEmpty()){
                    lbMessageNoAlarms.text = context.resources.getString(R.string.noAlarms)
                }else{
                    val result = mutableListOf<Alert>()
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
        // Solange Server noch nicht funktioniert; Keine genaue Beschreibung über den Response List of Alert-ObjectsList
        val result = mutableListOf<Alert>()
        /*val jsonArray = JSONArray()
        val json = JSONObject()
        json.put("ID", 390144198431043)
        json.put("Date", Date())
        json.put("Type", 0)
        json.put("State", 0)
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

        for(js: JSONObject in data){
            result.plus(Alert(
                js.get("ID") as Long,
                js.get("Date") as Date,
                js.get("Type") as Byte,
                js.get("State") as Byte,
                js.get("ClientId") as UUID,
                js.get("HelperId") as UUID?,
                js.get("DeviceId") as UUID?,
                js.get("TriggeringPositionLatitude") as Double?,
                js.get("TriggeringPositionLongitude") as Double?,
                js.get("TriggeringPositionTime") as Date?,
                js.get("CanBeForwarded") as Boolean
            ))
        }*/
        result.add(Alert(
            345365474563413,
            Date(),
            0,
            0,
            UUID(924389232,9340243),
            UUID(924389232,9340243), //HelperId
            null,
            46.6422491,
            14.2891614,
            null,
            false
        ))
        val adapter = AlertsListAdapter(result)
        rvAlarms.adapter = adapter
        adapter.notifyDataSetChanged()
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