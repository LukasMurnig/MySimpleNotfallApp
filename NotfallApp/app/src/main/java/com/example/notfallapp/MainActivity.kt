package com.example.notfallapp

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import com.example.notfallapp.BroadcastReciever.ActionsBracelet
import com.example.notfallapp.connectBracelet.AddBraceletActivity
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.INotifications
import com.example.notfallapp.server.ServerApi
import com.example.notfallapp.service.ForegroundServiceCreateSOSButton
import com.example.notfallapp.service.ServiceStartChecking
import com.example.notfallappLibrary.interfaces.VALRTIBracelet
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * MainActivity/HomeActivity, has the buttons to add a bracelet and give information if a bracelet is connected or not
 */
class MainActivity : AppCompatActivity(),
    ICreatingOnClickListener, INotifications, ICheckPermission, VALRTIBracelet {

    companion object {
        var context: Context? = null
        var timer: Timer = Timer()
    }

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var btnaddBracelet: ImageButton
    private lateinit var btnpairBracelet: ImageButton
    private lateinit var btnBracelet: ImageButton
    private lateinit var tvStatusbracelet: TextView
    private lateinit var tvaddbracelet: TextView
    private lateinit var tvpairbracelet: TextView

    private lateinit var handler: Handler
    private val actionsBracelet = ActionsBracelet()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureButtons()
        initComponents()
        try {
            checkState()
        }catch(ex: Exception){
            ex.toString()
        }
        val filter = IntentFilter()
        filter.addAction("ACTION_GATT_CONNECTED")
        filter.addAction("ACTION_GATT_DISCONNECTED")
        filter.addAction("Alarm called")
        filter.addAction("BatteryState")
        registerReceiver(actionsBracelet, filter)
        // make server ready
        GlobalScope.launch {
            try{
                ServerApi.getSharedPreferences()
            }catch (ex: UninitializedPropertyAccessException){
                ServerApi.setSharedPreferences(getSharedPreferences("Response", Context.MODE_PRIVATE))
            }

            if(ServerApi.volleyRequestQueue == null){
                ServerApi.volleyRequestQueue = Volley.newRequestQueue(applicationContext)
            }
        }

        // start the foregroundService which opens the notifcation with the SOS button
        ForegroundServiceCreateSOSButton.startForegroundService(applicationContext)

        checkGPSPermission()

        btnaddBracelet.setOnClickListener {
            Log.d("ButtonAdd", "Button Add bracelet was clicked in MainActivity")
            val intent = Intent(this, AddBraceletActivity::class.java)
            startActivity(intent)
            timer.cancel()
        }

        btnpairBracelet.setOnClickListener {
            val success = valrtHasSelectedDevice(this)
            if(success){
                tvStatusbracelet.textSize = 20F
                tvStatusbracelet.text = this.getString(R.string.tryToConnectBracelet)
                try {
                    timer.cancel()
                }catch (ex: Exception){
                    ex.toString()
                }
                valrtConnectToSelectedDevice(this, false, {
                    tvStatusbracelet.text = this.getString(R.string.braceleterror)
                },{
                    try {
                        timer = Timer()
                        checkState()
                    }catch(ex: java.lang.Exception){
                        Log.e("TAG", ex.toString())
                    }
                })
            }else{
                tvStatusbracelet.text = this.getString(R.string.noDeviceToPair)
            }
        }
    }

    /**
     * try to get the permission of the user for GPS
     */
    private fun checkGPSPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Log.i(
                            resources.getString(R.string.userpermission),
                            resources.getString(R.string.GPSPermissionGranted)
                        )
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT)
                    .show()
            }
            .onSameThread()
            .check()
    }

    private fun configureButtons() {
        // Button bar
        btnSos = findViewById(R.id.btn_sos)
        btnHome = findViewById(R.id.btnHome)
        btnHome.setImageResource(R.drawable.profil_active)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnSettings)
    }

    private fun initComponents() {
        btnaddBracelet = findViewById(R.id.btn_add_bracelet)
        btnpairBracelet = findViewById(R.id.btn_pair_bracelet)
        btnBracelet = findViewById(R.id.btn_bracelet)
        tvStatusbracelet = findViewById(R.id.tvStatusbracelet)
        tvaddbracelet = findViewById(R.id.tvaddbracelet)
        tvpairbracelet = findViewById(R.id.tvpairbracelet)

        handler = Handler(this.mainLooper)
        context = this
        CurrentLocation.getCurrentLocation(this)
        checkInternetGPSPermissions(this)
        val intent = Intent(this, ServiceStartChecking::class.java)
        context?.startService(intent)
    }

    /**
     * see if bracelet is connected or not and inform the user
     */
    private fun checkConnected(){
        tvStatusbracelet.textSize = 30F
        if (ActionsBracelet.connected){
            tvStatusbracelet.text = resources.getString(R.string.braceleteconnected)
            btnBracelet.visibility = View.VISIBLE
        }else{
            tvStatusbracelet.text = resources.getString(R.string.nobraceletconnected)
            btnBracelet.visibility = View.GONE
        }
    }

    private fun checkState(){
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    checkConnected()
                }
            }
        }, 0, 2000)
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(actionsBracelet)
    }
}

