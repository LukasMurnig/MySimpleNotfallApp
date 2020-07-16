package com.example.notfallapp.alarm

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.provider.Settings
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.room.Room
import com.example.notfallapp.R
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.database.AlarmDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class TimerHandler {
    companion object {
            private lateinit var handler: Handler
        private const val CHANNEL_ID = "144NA"

            fun timerHandler(context: Context){
                // this, when you would like to have the timer in the main thread
                //handler = Handler(Looper.getMainLooper())
                handler = Handler()
                handler.postDelayed({ //Do something after 10000ms
                    // here must the alarm send to the server


                    // create alarm in DB
                    createAlarmInDb(context)

                    createSuccessfulNotification(context)
                    val intent = Intent(context, AlarmSuccesfulActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, intent, null)
                }, 10000)
            }

            fun deleteTimer(){
                handler.removeCallbacksAndMessages(null)
            }

        private fun createAlarmInDb(context: Context){
            val androidId: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val clickedTime: Date = Calendar.getInstance().time

            val alarm = Alarm(androidId, "IAmATest", clickedTime.toString())

            val db = Room.databaseBuilder(context, AlarmDatabase::class.java, "alarms.db").fallbackToDestructiveMigration().build()
            try{
                GlobalScope.launch {
                    // zum Testen
                    db.alarmsDao().deleteAll()

                    db.alarmsDao().insertAlarm(alarm)
                }
            }catch (ex: Exception){
                println("Konnte Alarm nicht speichern. Grund: $ex")
            }
        }

        private fun createSuccessfulNotification(context: Context){
            val notificationLayout = RemoteViews(context.packageName, R.layout.notification_successful_alarm)

            val intentSuccesful = Intent(context, AlarmSuccesfulActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntentSuccesful: PendingIntent = PendingIntent.getActivity(context, 0, intentSuccesful, 0)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notfallapplogo)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
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