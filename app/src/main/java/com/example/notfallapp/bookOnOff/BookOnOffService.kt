package com.example.notfallapp.bookOnOff

import android.content.Context
import android.location.Location
import android.os.Handler
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.BeaconInRange
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.interfaces.WifiInRange
import com.example.notfallapp.server.ServerBookOnOff
import com.example.notfallapp.server.ServerCallAlarm
import com.example.notfallapp.server.ServerCheckpoint
import com.google.android.gms.tasks.Tasks.await
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import kotlinx.coroutines.*
import kotlin.system.*


class BookOnOffService {

    companion object{
        var bookURL = "checkin"
    }
    private var bookOn: Boolean = true
    var time: Long = 5000
    var sb = BeaconInRange()
    var wb = WifiInRange()
    fun bookOnOff(bookOn: Boolean, context: Context){
        this.bookOn = bookOn
        if (bookOn) bookURL = context.getString(R.string.checkIn)
        else bookURL = context.getString(R.string.checkout)
        startWifiScan(context)
    }

    fun stopScan(){
        BeaconInRange.stopSearchingBeacons()
        WifiInRange.stopScanning()
    }

    fun startScanBluetoothBeacon(context: Context){
        sb.getBeacon(context)
        Timer().schedule(object : TimerTask(){
            override fun run() {
                checkBluetoothBeacon(context)
            }
        }, time)
    }

    fun checkBluetoothBeacon(context: Context){
        BeaconInRange.stopSearchingBeacons()
        if (BeaconInRange.beacons.size >=1) ServerBookOnOff.checkLocationBluetoothBeacon(context)
        else startGPSScan(context)
    }
    fun startWifiScan(context: Context){
        wb.getWifiBeacons(context)
        Timer().schedule(object : TimerTask(){
            override fun run() {
                checkWifiScanResult(context)
            }
        }, time)
    }

    fun checkWifiScanResult(context: Context){
        WifiInRange.stopScanning()
        if (WifiInRange.wifiBeacon.size >= 1) ServerBookOnOff.checkLocationWifiBeacon(context)
        else startScanBluetoothBeacon(context)
    }

    fun startGPSScan(context: Context) = runBlocking{
        if (CurrentLocation.currentLocation != null) ServerCheckpoint.checkLocationGeoZone(context)
        CurrentLocation.getCurrentLocation(context)

    }
}