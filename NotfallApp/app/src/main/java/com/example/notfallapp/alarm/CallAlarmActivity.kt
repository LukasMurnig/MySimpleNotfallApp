package com.example.notfallapp.alarm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.notfallapp.R
import com.example.notfallapp.connectBracelet.AddBraceletActivity
import com.example.notfallapp.interfaces.checkPermission
import com.example.notfallapp.service.ServiceCancelAlarm
import java.lang.Exception
import kotlin.math.roundToInt


class CallAlarmActivity : AppCompatActivity(), checkPermission {
    private lateinit var btnCancelAlarm: Button
    private lateinit var tvAlarm: TextView
    private lateinit var tvConnectionState: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvLatitude: TextView
    private lateinit var tvAccuracy: TextView
    private lateinit var tvBattery: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_alarm)

        initComponents()

        btnCancelAlarm.setOnClickListener {
            Log.d(resources.getString(R.string.CancelButton),
                  String.format(resources.getString(R.string.CancelButtonClicked),
                                resources.getString(R.string.CallAlarm)))

            // start service cancel alarm, which also stop the timer;
            val intent = Intent(this, ServiceCancelAlarm::class.java)
            startService(intent)
        }

        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        if(activeNetwork?.isConnected == true){
            tvConnectionState.text = resources.getText(R.string.connected)
        }else{
            tvConnectionState.text = resources.getText(R.string.notConnected)
        }

        val batteryState = AddBraceletActivity.batteryState
        if(batteryState == " "){
            tvBattery.text = resources.getText(R.string.notConnected)
        }else{
            tvBattery.text = batteryState + "%"
        }
        getLatestKnownLocation()
    }

    private fun getLatestKnownLocation(){
        // get position
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
        val location =  lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        var longitude: String?
        var latitude: String?
        var accuracy: Float?
        try{
            longitude = location.longitude.toString()
            latitude = location.latitude.toString()
            accuracy = location.accuracy
        }catch (e: Exception){
            longitude = "N/A"
            latitude = "N/A"
            accuracy = 0.0F
        }
        //val verticalAccuracyMeters = getVerticalAccuracyMeters(location)

        tvLongitude.text = longitude.toString()
        tvLatitude.text = latitude.toString()

        if (accuracy != null) {
            if(accuracy.roundToInt()< accuracy){
                tvAccuracy.text = (accuracy.roundToInt()+1).toString()
            }else{
                tvAccuracy.text = accuracy.roundToInt().toString()
            }
        }

        tvAccuracy.text = tvAccuracy.text as String + " m"
    }

    private fun getVerticalAccuracyMeters(location:Location): Float{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            location.verticalAccuracyMeters
        }else{
            0.0F
        }

    }

    private fun initComponents(){
        btnCancelAlarm = findViewById(R.id.btn_cancel_alarm)
        tvAlarm = findViewById(R.id.tvAlarm)
        tvConnectionState = findViewById(R.id.tvConnection)
        tvLongitude = findViewById(R.id.tvLongitude)
        tvLatitude = findViewById(R.id.tvLatitude)
        tvAccuracy = findViewById(R.id.tvAccuracy)
        tvBattery = findViewById(R.id.tvBattery)
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        checkInternetAccess(this, connectivityManager, wifi)
    }
}