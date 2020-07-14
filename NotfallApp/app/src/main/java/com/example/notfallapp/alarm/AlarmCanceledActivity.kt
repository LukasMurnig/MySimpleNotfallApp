package com.example.notfallapp.alarm

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.RemoteViews
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener

class AlarmCanceledActivity : AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnMap: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var btnCancelAlarmOk: Button
    private lateinit var tvCanceledAlarm: TextView

    private val CHANNEL_ID = "144NA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // delete Timer of CallAlarmActivity
        TimerHandler.deleteTimer()

        setContentView(R.layout.activity_call_alarm_canceled)
        configureButtons()
        initComponents()

        createNotification()

        btnCancelAlarmOk.setOnClickListener() {
            Log.d("ButtonOk", "Button ok in AlarmCanceledActivity clicked!")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createNotification(){
        val notificationLayout = RemoteViews(packageName, R.layout.notification_alarm_canceled)

        val intent = Intent(this, AlarmCanceledActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)){
            notify(1444, builder.build())
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
        btnMap = findViewById(R.id.btnMap)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnMap, btnSettings)
    }
}