package com.example.notfallapp.alarm

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.R


class TimerHandler {
    companion object {
            private lateinit var handler: Handler
        private val CHANNEL_ID = "144NA"

            fun timerHandler(context: Context){
                // this, when you would like to have the timer in the main thread
                //handler = Handler(Looper.getMainLooper())
                handler = Handler()
                handler.postDelayed({ //Do something after 10000ms
                    // here must the alarm send to the server


                    createSuccesfulNotification(context)
                    val intent = Intent(context, AlarmSuccesfulActivity::class.java)
                    startActivity(context, intent, null)
                }, 10000)
            }

            fun deleteTimer(){
                handler.removeCallbacksAndMessages(null)
            }

        private fun createSuccesfulNotification(context: Context){
            val notificationLayout = RemoteViews(context.packageName, R.layout.notification_successful_alarm)

            val intentSuccesful = Intent(context, AlarmSuccesfulActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntentSuccesful: PendingIntent = PendingIntent.getActivity(context, 0, intentSuccesful, 0)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notfallapplogo)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntentSuccesful)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)){
                notify(4444, builder.build())
            }
        }

    }
}