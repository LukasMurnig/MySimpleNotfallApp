package com.example.notfallapp.alarm

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.checkPermission

class AlarmSuccesfulActivity : AppCompatActivity(), checkPermission {

    private lateinit var tvAlarm: TextView
    private lateinit var buttonsuccesfulOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_call_alarm_succesful)
        initComponents()

        buttonsuccesfulOk.setOnClickListener {
            Log.d(resources.getString(R.string.SOSButton),
                  String.format(resources.getString(R.string.SOSButtonClicked),
                                resources.getString(R.string.AlarmSuccessful)))
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initComponents() {
        tvAlarm = findViewById(R.id.tvAlarm)
        buttonsuccesfulOk = findViewById(R.id.btn_alarm_succesful_ok)
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        checkInternetAccess(this, connectivityManager, wifi)
    }
}