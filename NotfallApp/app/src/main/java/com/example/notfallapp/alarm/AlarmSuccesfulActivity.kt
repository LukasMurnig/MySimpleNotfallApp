package com.example.notfallapp.alarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R

class AlarmSuccesfulActivity : AppCompatActivity() {

    private lateinit var tvAlarm: TextView
    private lateinit var buttonsuccesfulOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_call_alarm_succesful)
        initComponents()

        buttonsuccesfulOk.setOnClickListener() {
            Log.d("ButtonOk", "Button Ok was clicked in AlarmSuccesfulActivity")
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initComponents() {
        tvAlarm = findViewById(R.id.tvAlarm)
        buttonsuccesfulOk = findViewById(R.id.btn_alarm_succesful_ok)
    }
}