package com.example.notfallapp.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notfallapp.R
import com.example.notfallapp.alarm.AlarmCanceledActivity
import com.example.notfallapp.alarm.TimerHandler

class ServiceCancelAlarm: Service() {
    private val CHANNEL_ID = "NA12345"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags2: Int, startId: Int): Int {
        // stop timer, before the timer can send to the server
        TimerHandler.deleteTimer()

        createCanceledAlarmNotification()

        val intentOnSos = Intent(applicationContext, AlarmCanceledActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intentOnSos.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intentOnSos)

        return super.onStartCommand(intent, flags2, startId)
    }

    private fun createCanceledAlarmNotification(){
        val notificationLayout = RemoteViews(packageName, R.layout.notification_alarm_canceled)

        val intent = Intent(this, AlarmCanceledActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notfallapplogo)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)){
            notify(4444, builder.build())
        }
    }
}