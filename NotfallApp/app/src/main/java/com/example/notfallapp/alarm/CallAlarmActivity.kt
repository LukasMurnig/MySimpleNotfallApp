package com.example.notfallapp.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RemoteViews
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.R
import com.example.notfallapp.service.ServiceCallAlarm
import com.example.notfallapp.service.ServiceCancelAlarm
import com.google.android.gms.cast.CastRemoteDisplayLocalService

class CallAlarmActivity : AppCompatActivity(){
    private lateinit var btnCancelAlarm: Button
    private lateinit var tvAlarm: TextView

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_alarm)

        initComponents()

        btnCancelAlarm.setOnClickListener() {
            Log.d("CancelButtonClicked", "Cancel Button in CallAlarmActivity clicked")

            // start service cancel alarm, which also stop the timer;
            val intent = Intent(this, ServiceCancelAlarm::class.java)
            startService(intent)
        }
    }

    private fun initComponents(){
        btnCancelAlarm = findViewById(R.id.btn_cancel_alarm)
        tvAlarm = findViewById(R.id.tvAlarm)
    }
}