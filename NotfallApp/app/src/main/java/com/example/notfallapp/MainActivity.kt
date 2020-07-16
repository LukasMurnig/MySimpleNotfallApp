package com.example.notfallapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.notfallapp.Login.SignUpActivity
import com.example.notfallapp.connectBracelet.AddBraceletActivity
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.INotificationCreateAlarm

class MainActivity : AppCompatActivity(),
    ICreatingOnClickListener, INotificationCreateAlarm {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var btnaddBracelet: ImageButton
    private lateinit var tvStatusbracelet: TextView
    private lateinit var tvaddbracelet: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureButtons()
        initComponents()
        var state: Boolean = AddBraceletActivity.connected
        println(state)
        if (state){
            tvStatusbracelet.text = getResources().getString(R.string.braceleteconnected)
        }

        btnaddBracelet.setOnClickListener() {
            Log.d("ButtonAdd", "Button Add bracelet was clicked in MainActivity")
            var intent = Intent(this, AddBraceletActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configureButtons(){
        // Button bar
        btnSos = findViewById(R.id.btn_sos)
        btnHome = findViewById(R.id.btnHome)
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
    }
}

