package com.example.notfallapp.interfaces

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.R
import com.example.notfallapp.alarm.CallAlarmActivity

// create a notification with a sos button to send a alarm
interface INotificationCreateAlarm {

    fun createNotificationCreateAlarm(context: Context){
        val CHANNEL_ID: String = "144NA"

        createNotificationChannel(context, CHANNEL_ID)

        val intent = Intent(context, CallAlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setContentTitle("Notfall App")
            .setContentText("First Test")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setTicker("Alarm")
            .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            .addAction(R.drawable.contacts, context.getString(R.string.sos),
                pendingIntent);

        // show notification
        with(NotificationManagerCompat.from(context)){
            notify(1444, builder.build())
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