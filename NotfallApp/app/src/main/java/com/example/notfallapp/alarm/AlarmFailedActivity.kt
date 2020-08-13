package com.example.notfallapp.alarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.notfallapp.R
import com.example.notfallapp.service.ServiceCallAlarm
import java.util.*

/*
 * Activity shows that the alarm failed and tries to start the alarm again
 */
class AlarmFailedActivity: Activity() {
    private lateinit var tvAlarm: TextView
    private lateinit var btnRetry: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_failed)

        tvAlarm = findViewById(R.id.tvAlarmFailed)
        btnRetry = findViewById(R.id.btn_stop_alarm)

        val context = this
        val timer = Timer()
        timer.schedule(object: TimerTask() {
            override fun run() {
                // Service opens Call Alarm Activity
                val intent = Intent(context, ServiceCallAlarm::class.java)
                context.startService(intent)
            }
        },0,5000)

        btnRetry.setOnClickListener {
            timer.cancel()
            val intent = Intent(this, AlarmCanceledActivity::class.java)
            this.startService(intent)
        }
    }
}