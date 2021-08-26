package com.example.notfallapp.interfaces

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager

class WifiInRange {

    companion object{
        var wifiManager: WifiManager? = null
        var wifiScanReceiver: BroadcastReceiver? = null
        var intentFilter: IntentFilter? = null
        var wifiBeacon = ArrayList<ScanResult>()
        var recieverRegistered: Boolean = false
        fun stopScanning(){
            wifiScanReceiver = null
            wifiManager = null
            intentFilter = null
        }
    }

    fun getWifiBeacons(context: Context){
        setVariables(context)
        intentFilter?.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        if (!recieverRegistered) {
            recieverRegistered = true
            context.registerReceiver(wifiScanReceiver, intentFilter)
        }

        val success = wifiManager?.startScan()
        if (!success!!) {
            // scan failure handling
            scanFailure()
        }
    }

    private fun setVariables(context: Context){
        if (wifiManager == null) wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiScanReceiver == null) setScanReceiver()
        if (intentFilter == null) intentFilter = IntentFilter()
    }

    private fun setScanReceiver(){
        wifiScanReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success == true) {
                    scanSuccess()
                } else {
                    scanFailure()
                }
            }

        }
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager?.scanResults
    }

    private fun scanSuccess() {
        wifiBeacon = wifiManager?.scanResults as ArrayList<ScanResult>
    }


}