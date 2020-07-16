package com.example.notfallapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.R
import com.example.notfallapp.alarm.CallAlarmActivity
import com.example.notfallapp.alarm.TimerHandler

// open Call Alarm Activity
class ServiceCallAlarm: Service() {
    private val CHANNEL_ID = "NA12345"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags2: Int, startId: Int): Int {
        // start timer
        TimerHandler.timerHandler(applicationContext)

        createCalledAlarmNotification()

        val intentOnSos = Intent(applicationContext, CallAlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intentOnSos.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intentOnSos)

        return super.onStartCommand(intent, flags2, startId)
    }

    private fun createCalledAlarmNotification(){
        //createNotificationChannel()

        // when user click on button "Abbrechen", service cancel alarm open, which stop the alarm
        val intentSos=Intent(this, ServiceCancelAlarm::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentSos = PendingIntent.getService(this, 4444, intentSos, PendingIntent.FLAG_CANCEL_CURRENT)

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

        // getting the layout of the notification
        val notificationLayout = RemoteViews(packageName, R.layout.notification_call_alarm)
        notificationLayout.setOnClickPendingIntent(R.id.btnCancelNotificationAlarm, pendingIntentSos)
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
}