package com.example.notfallapp.alarm

import android.app.PendingIntent
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RemoteViews
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R

class AlarmSuccesfulActivity : AppCompatActivity() {

    private lateinit var tvAlarm: TextView
    private lateinit var buttonsuccesfulOk: Button

    private val CHANNEL_ID = "144NA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_call_alarm_succesful)
        initComponents()

        createNotification()

        buttonsuccesfulOk.setOnClickListener() {
            Log.d("ButtonOk", "Button Ok was clicked in AlarmSuccesfulActivity")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createNotification(){
        val notificationLayout = RemoteViews(packageName, R.layout.notification_successful_alarm)

        val intent = Intent(this, AlarmSuccesfulActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)){
            notify(1444, builder.build())
        }
    }

    private fun initComponents() {
        tvAlarm = findViewById(R.id.tvAlarm)
        buttonsuccesfulOk = findViewById(R.id.btn_alarm_succesful_ok)
    }
}