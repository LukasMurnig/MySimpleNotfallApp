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
import com.example.notfallapp.service.ServiceCallAlarm

// create a notification with a sos button to send a alarm
interface INotificationCreateAlarm {

    val channelId: String
        get() = "NA12345"

    fun createNotificationCreateAlarm(context: Context){

        createNotificationChannel(context)

        // when user click on button "SOS", call service call alarm, which call alarm
        val intentCallAlarm=Intent(context, ServiceCallAlarm::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentCallAlarm = PendingIntent.getService(context, 4444, intentCallAlarm, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_sos)
        notificationLayout.setOnClickPendingIntent(R.id.btnNotSOS, pendingIntentCallAlarm)
        builder.setCustomContentView(notificationLayout).setCustomBigContentView(notificationLayout)

        // show notification
        with(NotificationManagerCompat.from(context)){
            notify(4444, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notificationTitle)
            val descriptionText = context.getString(R.string.notificationTitle)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =context.
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}