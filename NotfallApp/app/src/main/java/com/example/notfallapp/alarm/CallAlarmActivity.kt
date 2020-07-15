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
    private val CHANNEL_ID = "NA12345"

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_alarm)

        initComponents()

        // create Timer
        TimerHandler.timerHandler(this)

        createNotification()

        btnCancelAlarm.setOnClickListener() {
            TimerHandler.deleteTimer()
            Log.d("CancelButtonClicked", "Cancel Button in CallAlarmActivity clicked")
            val intent = Intent(this, AlarmCanceledActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createNotification(){
        //createNotificationChannel()

        val notificationLayout = RemoteViews(packageName, R.layout.notification_call_alarm)

        // when user click on button "Abbrechen", service cancel alarm open, which stop the alarm
        val i=Intent(this, ServiceCancelAlarm::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val p = PendingIntent.getService(this, 4444, i, PendingIntent.FLAG_CANCEL_CURRENT)

        // when user click on message, open CallAlarm Activity
        val intent = Intent(this, CallAlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // build custom notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setTicker("Alarm")
            .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            .setOngoing(true)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationLayout.setOnClickPendingIntent(R.id.btnCancelNotificationAlarm, p)
        builder.setCustomContentView(notificationLayout).setCustomBigContentView(notificationLayout)

        // show notification
        with(NotificationManagerCompat.from(this)){
            notify(4444, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notificationTitle)
            val descriptionText = getString(R.string.notificationTitle)
            val importance = NotificationManager.IMPORTANCE_HIGH
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