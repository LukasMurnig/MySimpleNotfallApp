package com.example.notfallapp.server

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.notfallapp.bookOnOff.BookOnOffService
import org.json.JSONObject

class ServerBookOnOff {
    companion object{
        private var volleyRequestQueue: RequestQueue? = null
        var userId: String? = null

        fun checkLocationBluetoothBeacon(context: Context) {
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            val reqBodyBeacon = JSONObject()
            val currentTime = ServerCallAlarm.createCurrentTimeStamp()
            reqBody.put("Timestamp", currentTime)
            reqBodyBeacon.put("Beacons", ServerCallAlarm.prepareBeaconsRequest(currentTime, 0))
            reqBody.put("Positions", reqBodyBeacon)
            ServerCallAlarm.createJSONRequest(
                context,
                "/timerecordings/${BookOnOffService.bookURL}",
                reqBody,
                0
            )
        }

        fun checkLocationWifiBeacon(context: Context){
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            val reqBodyBeacon = JSONObject()
            val currentTime = ServerCallAlarm.createCurrentTimeStamp()
            reqBody.put("Timestamp", currentTime)
            reqBodyBeacon.put("Beacons", ServerCallAlarm.prepareBeaconsRequest(currentTime, 1))
            reqBody.put("Positions", reqBodyBeacon)
            Log.e("Wifi-Request", reqBody.toString())
            ServerCallAlarm.createJSONRequest(
                context,
                "/timerecordings/${BookOnOffService.bookURL}",
                reqBody,
                0
            )
        }

        fun checkLocationGeoZone(context: Context){
            volleyRequestQueue = Volley.newRequestQueue(context)
            val reqBody = JSONObject()
            val reqBodyBeacon = JSONObject()
            val currentTime = ServerCallAlarm.createCurrentTimeStamp()
            reqBody.put("Timestamp", currentTime)
            reqBodyBeacon.put("Positions", ServerCallAlarm.prepareGPSRequest(currentTime))
            reqBody.put("Positions", reqBodyBeacon)
            Log.e("GPS-Request", reqBody.toString())
            ServerCallAlarm.createJSONRequest(
                context,
                "/timerecordings/${BookOnOffService.bookURL}",
                reqBody,
                0
            )
        }
    }
}