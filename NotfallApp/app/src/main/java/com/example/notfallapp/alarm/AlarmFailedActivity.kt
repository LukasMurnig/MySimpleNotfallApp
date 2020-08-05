package com.example.notfallapp.alarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.notfallapp.R
import com.example.notfallapp.service.ServiceCallAlarm

class AlarmFailedActivity: Activity() {
    private lateinit var tvAlarm: TextView
    private lateinit var btnRetry: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_failed)
        tvAlarm = findViewById(R.id.tvAlarmFailed)
        btnRetry = findViewById(R.id.btn_retry_alarm)

        btnRetry.setOnClickListener(){
            var intent = Intent(this, ServiceCallAlarm::class.java)
            this.startService(intent)
        }
    }
}