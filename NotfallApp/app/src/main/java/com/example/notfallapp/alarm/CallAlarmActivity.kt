package com.example.notfallapp.alarm

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.BroadcastReciever.ActionsBracelet
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.BeaconInRange
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.service.ServiceCancelAlarm
import org.altbeacon.beacon.Beacon
import kotlin.math.roundToInt

/**
 * Activity which opens when the user send SOS.
 **/
class CallAlarmActivity : AppCompatActivity(), ICheckPermission {
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
            TimerHandler.deleteTimer()

            Log.d(resources.getString(R.string.CancelButton),
                  String.format(resources.getString(R.string.CancelButtonClicked),
                                resources.getString(R.string.CallAlarm)))

            // start service cancel alarm, which also stop the timer;
            val intent = Intent(this, ServiceCancelAlarm::class.java)
            startService(intent)
            finish()
        }
        var sb = BeaconInRange()
        sb.getBeacon(this)
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        if(activeNetwork?.isConnected == true){
            tvConnectionState.text = resources.getText(R.string.connected)
        }else{
            tvConnectionState.text = resources.getText(R.string.notConnected)
        }
        if(ActionsBracelet.batteryState == 0){
            tvBattery.text = resources.getText(R.string.notConnected)
        }else{
            val s = ActionsBracelet.batteryState
            tvBattery.text = "$s%"
        }
        getLatestKnownLocation()
    }

    /**
     * Set the Last Location in our Activity to show the user his position
     */
    private fun getLatestKnownLocation(){
        val location = CurrentLocation.getCurrentLocation(applicationContext)
        var longitude: String?
        var latitude: String?
        var accuracy: Float?

        try{
            longitude = location!!.longitude.toString()
            latitude = location.latitude.toString()
            accuracy = location.accuracy
        }catch (e: Exception){
            longitude = "N/A"
            latitude = "N/A"
            accuracy = 0.0F
        }

        tvLongitude.text = longitude
        tvLatitude.text = latitude

        if (accuracy != null) {
            if(accuracy.roundToInt()< accuracy){
                tvAccuracy.text = (accuracy.roundToInt()+1).toString()
            }else{
                tvAccuracy.text = accuracy.roundToInt().toString()
            }
        }

        tvAccuracy.text = "${tvAccuracy.text} m"
    }

    /**
     * Initialize the Components for our current Activity
     */
    private fun initComponents(){
        btnCancelAlarm = findViewById(R.id.btn_cancel_alarm)
        tvAlarm = findViewById(R.id.tvAlarm)
        tvConnectionState = findViewById(R.id.tvConnection)
        tvLongitude = findViewById(R.id.tvLongitude)
        tvLatitude = findViewById(R.id.tvLatitude)
        tvAccuracy = findViewById(R.id.tvAccuracy)
        tvBattery = findViewById(R.id.tvBattery)
        checkInternetGPSPermissions(this)
    }
}