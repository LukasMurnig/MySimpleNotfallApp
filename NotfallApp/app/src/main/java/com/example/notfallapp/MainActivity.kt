package com.example.notfallapp

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.connectBracelet.AddBraceletActivity
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.INotifications
import com.example.notfallapp.interfaces.checkPermission
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class MainActivity : AppCompatActivity(),
    ICreatingOnClickListener, INotifications, checkPermission {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var btnaddBracelet: ImageButton
    private lateinit var tvStatusbracelet: TextView
    private lateinit var tvaddbracelet: TextView

    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureButtons()
        initComponents()

        checkState()

        checkGPSPermission()

        btnaddBracelet.setOnClickListener {
            Log.d("ButtonAdd", "Button Add bracelet was clicked in MainActivity")
            val intent = Intent(this, AddBraceletActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkGPSPermission(){
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Log.i(resources.getString(R.string.userpermission), resources.getString(R.string.GPSPermissionGranted))
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        //openSettingsDialog();
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

    private fun configureButtons(){
        // Button bar
        btnSos = findViewById(R.id.btn_sos)
        btnHome = findViewById(R.id.btnHome)
        btnHome.setImageResource(R.drawable.profil_active)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnSettings)
        createNotificationCreateAlarm(this)
    }

    private fun initComponents(){
        btnaddBracelet = findViewById(R.id.btn_add_bracelet)
        tvStatusbracelet = findViewById(R.id.tvStatusbracelet)
        tvaddbracelet = findViewById(R.id.tvaddbracelet)
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        checkInternetAccess(this, connectivityManager, wifi)
        handler = Handler()
    }

    private fun checkState(){
        handler.postDelayed({ //Do something after 2000ms
            // here check if Bracelet is connected
            var state: Boolean = AddBraceletActivity.connected
            if (state){
                tvStatusbracelet.text = getResources().getString(R.string.braceleteconnected)
            }
            checkState()
        }, 2000)
    }
}

