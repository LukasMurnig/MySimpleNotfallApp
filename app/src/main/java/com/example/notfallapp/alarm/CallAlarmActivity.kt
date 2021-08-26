package com.example.notfallapp.alarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.ebanx.swipebtn.SwipeButton
import com.example.notfallapp.MainActivity
import com.example.notfallapp.MainActivity.Companion.context
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.BeaconInRange
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.WifiInRange
import com.example.notfallapp.login.LoginActivity
import com.example.notfallapp.service.ServiceCancelAlarm
import org.altbeacon.beacon.BeaconData
import java.util.*
import kotlin.concurrent.schedule

/**
 * Activity which opens when the user send SOS.
 **/
class CallAlarmActivity : AppCompatActivity(), ICheckPermission {

    private lateinit var message: TextView
    private lateinit var alert_View: ImageView
    private lateinit var card_View: CardView
    private lateinit var lbAlertSlide: TextView
    private lateinit var swipe_btn: SwipeButton
    private lateinit var dataProtection: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_alarm)

        initComponents()
        TimerHandler.timerHandler(this)

        swipe_btn.setOnStateChangeListener() {
            if(it){
                TimerHandler.deleteTimer()

                Log.d(resources.getString(R.string.CancelButton),
                    String.format(resources.getString(R.string.CancelButtonClicked),
                        resources.getString(R.string.CallAlarm)))

                // start service cancel alarm, which also stop the timer;
                swipe_btn.toggleState()
                val intent = Intent(this, ServiceCancelAlarm::class.java)
                startService(intent)
                finish()
            }
        }

        var sb = BeaconInRange()
        sb.getBeacon(this)
        Timer("Timer",false).schedule(10000) {
            BeaconInRange.stopSearchingBeacons()
            if (BeaconInRange.beacons.size < 1){
                println(BeaconInRange.beacons.toString())
                //println(bluetooth.bluetoothAddress)
                //println(bluetooth.bluetoothName)
                //println(bluetooth.beaconTypeCode)
            }else{
                scanForWifiBeacons()
            }

        }
    }

    private fun scanForWifiBeacons(){
        var wb = WifiInRange()
        wb.getWifiBeacons(this)
        Timer("WifiScan", false).schedule(10000) {
            WifiInRange.stopScanning()
            CurrentLocation.searchForLocation()
        }
    }

    /**
     * Initialize the Components for our current Activity
     */
    private fun initComponents(){
        message = findViewById(R.id.message)
        alert_View = findViewById(R.id.alert_View)
        card_View = findViewById(R.id.card_View)
        lbAlertSlide = findViewById(R.id.lbAlertSlide)
        swipe_btn = findViewById(R.id.swipe_btn)
        dataProtection = findViewById(R.id.dataProtection)
        checkInternetGPSPermissions(this)
    }
}