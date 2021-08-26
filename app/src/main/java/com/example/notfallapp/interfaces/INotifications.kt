package com.example.notfallapp.interfaces

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.alarm.AlarmCanceledActivity
import com.example.notfallapp.alarm.AlarmSuccessfulActivity
import com.example.notfallapp.alarm.CallAlarmActivity
import com.example.notfallapp.service.ServiceCallAlarm
import com.example.notfallapp.service.ServiceCancelAlarm

/**
 * Interface which creates all Notifications of the app
 */
interface INotifications {

    private val channelIdLowPriority: String
        get() = "dafew32rfda"

    private val channelIdHighPriority: String
        get() = "htw5343wgd"

    private val notificationId: Int
        get() = 435624234

    private val notificationIdPermission: Int
        get() = 435624234

    /**
     * create Notification no internet
     */
    fun createNotificationNoInternet(context: Context){
        val builder = createBasicNotification(context, channelIdLowPriority, true)

        builder.setContentTitle(context.getString(R.string.noInternet))
            .setContentText(context.getString(R.string.enableInternet))

        showNotificationPermission(context, builder)
    }

    /**
     * create Notification no gps
     */
    fun createNotificationNoGPS(context: Context){
        val builder = createBasicNotification(context, channelIdHighPriority, true)

        builder.setContentTitle(context.getString(R.string.noGPSNotificationbar))
            .setContentText(context.getString(R.string.enableGPS))

        showNotificationPermission(context, builder)
    }

    /**
     * create Notification connection with the bracelet lost
     */
    fun createNotificationConnectionBraceletLost(context: Context){
        val builder = createBasicNotification(context, channelIdHighPriority, true)
        builder.setContentTitle(context.getString(R.string.lostConnection))
            .setContentText(context.getString(R.string.reconnectBracelet))

        showNotificationPermission(context, builder)
    }

    /**
     * create Notification with the SOS button
     */
    fun createNotificationCreateAlarm(context: Context): Notification {
        createNotificationChannel(context, NotificationManager.IMPORTANCE_DEFAULT, channelIdLowPriority)

        val notification = createBasicNotification(context, channelIdLowPriority, false)

        // when user click on button "SOS", call service call alarm, which call alarm
        val intentCallAlarm=Intent(context, ServiceCallAlarm::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentCallAlarm = PendingIntent.getService(context, 4444, intentCallAlarm, PendingIntent.FLAG_CANCEL_CURRENT)
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_sos)
        notificationLayout.setOnClickPendingIntent(R.id.btnNotSOS, pendingIntentCallAlarm)
        notification
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)

        val not = notification.build()
        not.flags = (Notification.FLAG_ONGOING_EVENT and Notification.FLAG_NO_CLEAR)
        //notification.flags or (Notification.FLAG_ONGOING_EVENT or Notification.FLAG_NO_CLEAR)
        return not
    }

    /**
     * create Notification alarm was successful
     */
    fun createNotificationSuccessfulAlarm(context: Context){
        val builder = createBasicNotification(context, channelIdHighPriority, true)

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_successful_alarm)
        builder
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)

        // Set the intent that will fire when the user taps the notification
        val intentSuccessful = Intent(context, AlarmSuccessfulActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentSuccessful: PendingIntent = PendingIntent.getActivity(context, 0, intentSuccessful, 0)
        builder.setContentIntent(pendingIntentSuccessful)

        showNotification(context, builder)
    }

    /**
     * create Notification the alarm was canceled
     */
    fun createNotificationCancelledAlarm(context: Context){
        val builder = createBasicNotification(context, channelIdHighPriority, true)

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_alarm_canceled)
        builder
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)

        // Set the intent that will fire when the user taps the notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        builder.setContentIntent(pendingIntent)

        showNotification(context, builder)
    }

    /**
     * create Notification a alarm will be send soon to the server
     */
    fun createNotificationAlarmOnGoing(context: Context){
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

    /**
     * function create the base notification of all notifications
     */
    fun createBasicNotification(context: Context, channelId: String, highPriority: Boolean): NotificationCompat.Builder{
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if(highPriority){
            val v = longArrayOf(500, 5000)
            builder
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(v)
                .setAutoCancel(true)
        }else{
            builder
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVibrate(longArrayOf(0, 0))
                .setOngoing(true)
        }

        return builder
    }

    /**
     * function shows the notification to the user
     */
    private fun showNotification(context: Context, notification: NotificationCompat.Builder){
        with(NotificationManagerCompat.from(context)){
            notify(notificationId, notification.build())
        }
    }
    /**
     * function shows the permission notification to the user
     */
    private fun showNotificationPermission(context: Context, notification: NotificationCompat.Builder){
        with(NotificationManagerCompat.from(context)){
            notify(notificationIdPermission, notification.build())
        }
    }

    /**
     * function create the notifcation channel which is required from a certain version to see the notification
     */
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