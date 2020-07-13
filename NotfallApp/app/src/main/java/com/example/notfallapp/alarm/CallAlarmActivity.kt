package com.example.notfallapp.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener

class CallAlarmActivity : AppCompatActivity(){


    private lateinit var btnCancelAlarm: Button
    private lateinit var tvAlarm: TextView
    private val CHANNEL_ID = "144NA"

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_alarm)

        initComponents()

        createNotificationChannel()

        val intent = Intent(this, CallAlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notificationLayout = RemoteViews(packageName, R.layout.notification_call_alarm)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.contacts)
            //.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)){
            notify(1444, builder.build())
        }

        var handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, AlarmSuccesfulActivity::class.java)
            startActivity(intent)
        },10000)

        btnCancelAlarm.setOnClickListener() {
            Log.d("CancelButtonClicked", "Cancel Button in CallAlarmActivity clicked")
            handler.removeCallbacksAndMessages(null)
            var intent = Intent(this, AlarmCanceledActivity::class.java)
            startActivity(intent)
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notificationTitle)
            val descriptionText = getString(R.string.notificationDescription)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initComponents(){
        btnCancelAlarm = findViewById(R.id.btn_cancel_alarm)
        tvAlarm = findViewById(R.id.tvAlarm)
    }
}