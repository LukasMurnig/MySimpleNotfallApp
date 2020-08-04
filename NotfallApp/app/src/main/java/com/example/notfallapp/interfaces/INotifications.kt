package com.example.notfallapp.interfaces

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.R
import com.example.notfallapp.alarm.AlarmCanceledActivity
import com.example.notfallapp.alarm.AlarmSuccesfulActivity
import com.example.notfallapp.alarm.CallAlarmActivity
import com.example.notfallapp.service.ServiceCallAlarm
import com.example.notfallapp.service.ServiceCancelAlarm


// create a notification with a sos button to send a alarm
interface INotifications {

    private val channelIdLowPriority: String
        get() = "aefewfw32rfsdaf"

    private val channelIdHighPriority: String
        get() = "dafewf23r2"

    private val notificationId: Int
        get() = 444444123

    fun createNotificationNoInternet(context: Context){
        createNotificationChannel(context, NotificationManager.IMPORTANCE_HIGH, channelIdHighPriority)

        val builder = createBasicNotification(context, channelIdHighPriority, true)

        builder.setContentTitle(context.getString(R.string.noInternet))
            .setContentText(context.getString(R.string.enableInternet))

        showNotification(context, builder)
    }

    fun createNotificationNoGPS(context: Context){
        createNotificationChannel(context, NotificationManager.IMPORTANCE_HIGH, channelIdHighPriority)

        var builder = createBasicNotification(context, channelIdHighPriority, true)

        builder.setContentTitle(context.getString(R.string.noGPSNotificationbar))
            .setContentText(context.getString(R.string.enableGPS))

        showNotification(context, builder)
    }

    fun createNotificationConnectionBraceletLost(context: Context){
        createNotificationChannel(context, NotificationManager.IMPORTANCE_HIGH, channelIdHighPriority)

        var builder = createBasicNotification(context, channelIdHighPriority, true)
        builder.setContentTitle(context.getString(R.string.lostConnection))
            .setContentText(context.getString(R.string.reconnectBracelet))

        showNotification(context, builder)
    }

    fun createNotificationCreateAlarm(context: Context): Notification {
        createNotificationChannel(context, NotificationManager.IMPORTANCE_DEFAULT, channelIdLowPriority)

        // when user click on button "SOS", call service call alarm, which call alarm
        val intentCallAlarm=Intent(context, ServiceCallAlarm::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentCallAlarm = PendingIntent.getService(context, 4444, intentCallAlarm, PendingIntent.FLAG_CANCEL_CURRENT)
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_sos)
        notificationLayout.setOnClickPendingIntent(R.id.btnNotSOS, pendingIntentCallAlarm)

        val notification: Notification = NotificationCompat.Builder(context, channelIdLowPriority)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout).build()

        return notification
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

        showNotification(context, builder)
    }

    fun createNotificationCancelledAlarm(context: Context){
        val mp = MediaPlayer.create(context, R.raw.alarm_canceled)
        mp.start()
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
        val mp: MediaPlayer = MediaPlayer.create(context, R.raw.alarm_called)
        mp.start()
        createNotificationChannel(context, NotificationManager.IMPORTANCE_HIGH, channelIdHighPriority)

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

    fun createBasicNotification(context: Context, channelId: String, highPriority: Boolean): NotificationCompat.Builder{
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

    private fun createNotificationChannel(context: Context, importance: Int, channelId: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notificationTitle)
            val descriptionText = context.getString(R.string.notificationTitle)

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = context.
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}