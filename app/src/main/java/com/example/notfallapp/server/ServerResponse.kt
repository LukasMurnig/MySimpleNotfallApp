package com.example.notfallapp.server

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.toolbox.HttpHeaderParser
import com.example.notfallapp.MainActivity
import com.example.notfallapp.MainActivity.Companion.context
import com.example.notfallapp.bookOnOff.BookOnOffService
import org.json.JSONObject
import java.nio.charset.StandardCharsets.UTF_8

class ServerResponse {
    companion object {
        val mainActivity = MainActivity()
        var resultMessage: String = ""
        var bookOnOffService = BookOnOffService()
        fun recievedServerResponse(response: JSONObject?, responseError: NetworkResponse?, type: Int){
            if (response != null) startSuccessDialog(response)
            else{
                if (responseError?.statusCode == 500) Log.e("Server-Error", responseError.toString())
                if (responseError!!.statusCode in 400..499){
                    val data = responseError.data?.toString(UTF_8)
                    val jsonObject = JSONObject(data!!)
                    responseError.statusCode
                    resultMessage = jsonObject.get("ResultMessage") as String
                }
                when (type) {
                    0 -> startWifiScan()
                    1 -> startBluetoothScan()
                    2 -> startGPSScan()
                    3 -> checkGPSResult(response, responseError)
                }
            }
        }

        fun startWifiScan(){
            context?.let { bookOnOffService.startWifiScan(it) }
        }

        fun startBluetoothScan(){
            context?.let { bookOnOffService.startScanBluetoothBeacon(it) }
        }

        fun startGPSScan(){
            context?.let { bookOnOffService.startGPSScan(it) }
        }

        fun checkGPSResult(response: JSONObject?, responseError: NetworkResponse?){
            if (response != null) startSuccessDialog(response)
            else{
                if (responseError?.statusCode == 500) Log.e("Server-Error", responseError.toString())
                if (responseError!!.statusCode in 400..499){
                    val data = responseError?.data?.toString(UTF_8)
                    val jsonObject = JSONObject(data!!)
                    responseError.statusCode
                    resultMessage = jsonObject.get("ResultMessage") as String
                }

            }
        }

        fun startSuccessDialog(response: JSONObject?){
            val responseMessage = response?.get("ResultMessage") as String
            if (responseMessage == "Ok") mainActivity.createSuccessDialog()
        }
    }
}