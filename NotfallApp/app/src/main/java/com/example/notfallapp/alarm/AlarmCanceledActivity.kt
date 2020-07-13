package com.example.notfallapp.alarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener

class AlarmCanceledActivity : AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var btnCancelAlarmOk: Button
    private lateinit var tvCanceledAlarm: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_call_alarm_canceled)
        configureButtons()
        initComponents()

        btnCancelAlarmOk.setOnClickListener() {
            Log.d("ButtonOk", "Button ok in AlarmCanceledActivity clicked!")
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initComponents() {
        btnCancelAlarmOk = findViewById(R.id.btn_cancel_alarm_ok)
        tvCanceledAlarm = findViewById(R.id.tvCanceledAlarm)
    }

    private fun configureButtons() {
        // SOS Button
        btnSos = findViewById(R.id.btn_sos)

        // Button bar
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnSettings)
    }
}