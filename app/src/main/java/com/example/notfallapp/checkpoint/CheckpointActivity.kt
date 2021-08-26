package com.example.notfallapp.checkpoint

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.BeaconInRange
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.interfaces.WifiInRange
import com.example.notfallapp.server.ServerCheckpoint
import java.util.*
import java.util.concurrent.Executors

class CheckpointActivity: AppCompatActivity() {

    private lateinit var backBtn: Button
    private lateinit var ivCheckpoint: ImageView
    private lateinit var tvCheckpoint: TextView
    private lateinit var context: Context
    private var time = 10000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkpoint)

        initializeComponents()

        backBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        startScan()
    }

    private fun startScan() {
        var sb = BeaconInRange()
        sb.getBeacon(this)
        var timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                BeaconInRange.stopSearchingBeacons()
                if (BeaconInRange.beacons.size >= 1){
                    ServerCheckpoint.checkLocationBluetoothBeacon(context)
                }else{
                    startWifiScan()
                }
            }
        },time)
    }

    private fun startWifiScan() {
        ivCheckpoint.setImageResource(R.drawable.wifi_beacon)
        tvCheckpoint.text = resources.getText(R.string.checkWifiBeacon)
        var wb = WifiInRange()
        wb.getWifiBeacons(this)
        var timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                WifiInRange.stopScanning()
                if (WifiInRange.wifiBeacon.size >= 1) {
                    ServerCheckpoint.checkLocationWifiBeacon(context)
                }else{
                    startGPSScan()
                }
            }

        },time)
    }

    private fun startGPSScan() {
        ivCheckpoint.setImageResource(R.drawable.check_gps)
        tvCheckpoint.text = resources.getText(R.string.checkGPSLocation)
        if (CurrentLocation.currentLocation != null){
            ServerCheckpoint.checkLocationGeoZone(context)
        }
        val executor = Executors.newFixedThreadPool(5)
        val worker = Runnable {
            CurrentLocation.getCurrentLocation(context
            )
            ServerCheckpoint.checkLocationGeoZone(context)
        }
        executor.execute(worker)
        executor.shutdown()
    }

    private fun initializeComponents() {
        backBtn = findViewById(R.id.backBtn)
        ivCheckpoint = findViewById(R.id.ivCheckpoint)
        tvCheckpoint = findViewById(R.id.tvCheckpoint)
        context = this
    }
}