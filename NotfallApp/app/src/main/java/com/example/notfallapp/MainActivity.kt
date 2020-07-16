package com.example.notfallapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.notfallapp.Login.SignUpActivity
import com.example.notfallapp.alarm.AlarmSuccesfulActivity
import com.example.notfallapp.alarm.TimerHandler
import com.example.notfallapp.connectBracelet.AddBraceletActivity
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.INotificationCreateAlarm

class MainActivity : AppCompatActivity(),
    ICreatingOnClickListener, INotificationCreateAlarm {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnMap: ImageButton
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
        btnMap = findViewById(R.id.btnMap)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnMap, btnSettings)
        createNotificationCreateAlarm(this)
    }

    private fun initComponents(){
        btnaddBracelet = findViewById(R.id.btn_add_bracelet)
        tvStatusbracelet = findViewById(R.id.tvStatusbracelet)
        tvaddbracelet = findViewById(R.id.tvaddbracelet)
        handler = Handler()
    }

    private fun checkState(){
        handler.postDelayed({ //Do something after 2000ms
            // here check if Bracelet is connected
            var state: Boolean = AddBraceletActivity.connected
            if (state){
                tvStatusbracelet.text = getResources().getString(R.string.braceleteconnected)
            }

        }, 2000)
    }
}

