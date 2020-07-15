package com.example.notfallapp.interfaces

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.R
import com.example.notfallapp.alarm.CallAlarmActivity
import com.example.notfallapp.service.ServiceCallAlarm

// create a notification with a sos button to send a alarm
interface INotificationCreateAlarm {

    fun createNotificationCreateAlarm(context: Context){
        val CHANNEL_ID: String = "NA12345"

        createNotificationChannel(context, CHANNEL_ID)

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_sos)

        // when user click on button "SOS", call service call alarm, which call alarm
        val i=Intent(context, ServiceCallAlarm::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val p = PendingIntent.getService(context, 4444, i, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)

        notificationLayout.setOnClickPendingIntent(R.id.btnNotSOS, p)
        builder.setCustomContentView(notificationLayout).setCustomBigContentView(notificationLayout)

        // show notification
        with(NotificationManagerCompat.from(context)){
            notify(4444, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context, CHANNEL_ID: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notificationTitle)
            val descriptionText = context.getString(R.string.notificationTitle)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =context.
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}