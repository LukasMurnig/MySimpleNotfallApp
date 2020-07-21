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
import com.example.notfallapp.alarm.AlarmCanceledActivity
import com.example.notfallapp.alarm.AlarmSuccesfulActivity
import com.example.notfallapp.alarm.CallAlarmActivity
import com.example.notfallapp.alarm.TimerHandler
import com.example.notfallapp.service.ServiceCallAlarm
import com.example.notfallapp.service.ServiceCancelAlarm

// create a notification with a sos button to send a alarm
interface INotifications {

    val channelIdLowPriority: String
        get() = "fkaieoweonfa"

    val channelIdHighPriority: String
        get() = "dklqneoqod"

    private val notificationId: Int
        get() = 444444

    fun createNotificationCreateAlarm(context: Context){
        createLowNotificationChannel(context)
        //createHighNotificationChannel(context)

        val builder = createBasicNotification(context, channelIdLowPriority, false)

        // when user click on button "SOS", call service call alarm, which call alarm
        val intentCallAlarm=Intent(context, ServiceCallAlarm::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentCallAlarm = PendingIntent.getService(context, 4444, intentCallAlarm, PendingIntent.FLAG_CANCEL_CURRENT)
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_sos)
        notificationLayout.setOnClickPendingIntent(R.id.btnNotSOS, pendingIntentCallAlarm)
        builder
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)

        showNotification(context, builder)
    }

    fun createNotificationSuccessfulAlarm(context: Context){
        val builder = createBasicNotification(context, channelIdHighPriority, true)

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_successful_alarm)
        builder
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)

        // Set the intent that will fire when the user taps the notification
        val intentSuccessful = Intent(context, AlarmSuccesfulActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentSuccessful: PendingIntent = PendingIntent.getActivity(context, 0, intentSuccessful, 0)
        builder.setContentIntent(pendingIntentSuccessful)

        with(NotificationManagerCompat.from(context)){
            notify(444444, builder.build())
        }
    }

    fun createNotificationCancelledAlarm(context: Context){
        val builder = createBasicNotification(context, channelIdHighPriority, true)

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_alarm_canceled)
        builder
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)

        // Set the intent that will fire when the user taps the notification
        val intent = Intent(context, AlarmCanceledActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        builder.setContentIntent(pendingIntent)

        showNotification(context, builder)
    }

    fun createNotificationAlarmOnGoing(context: Context){
        createHighNotificationChannel(context)

        val builder = createBasicNotification(context, channelIdHighPriority, true)
        builder
            .setTicker("Alarm")
            .setOngoing(true)

        // Set the intent that will fire when the user taps the notification
        val intent = Intent(context, CallAlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        builder.setContentIntent(pendingIntent)

        // when user click on button "Abbrechen", service cancel alarm open, which stop the alarm
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_call_alarm)
        val intentSos=Intent(context, ServiceCancelAlarm::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentSos = PendingIntent.getService(context, 4444, intentSos, PendingIntent.FLAG_CANCEL_CURRENT)
        notificationLayout.setOnClickPendingIntent(R.id.btnCancelNotificationAlarm, pendingIntentSos)
        builder.setCustomContentView(notificationLayout).setCustomBigContentView(notificationLayout)

        showNotification(context, builder)
    }

    private fun createBasicNotification(context: Context, channelId: String, highPriority: Boolean): NotificationCompat.Builder{
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if(highPriority){
            builder
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                .setAutoCancel(true)
        }else{
            builder
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
        }

        return builder
    }

    private fun showNotification(context: Context, notification: NotificationCompat.Builder){
        with(NotificationManagerCompat.from(context)){
            notify(notificationId, notification.build())
        }
    }

    private fun createLowNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notificationTitle)
            val descriptionText = context.getString(R.string.notificationTitle)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelIdLowPriority, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = context.
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createHighNotificationChannel(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notificationTitle)
            val descriptionText = context.getString(R.string.notificationTitle)

            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelIdHighPriority, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = context.
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}