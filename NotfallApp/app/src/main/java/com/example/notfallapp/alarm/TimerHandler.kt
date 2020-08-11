package com.example.notfallapp.alarm

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.core.content.ContextCompat.startActivity
import com.example.notfallapp.interfaces.INotifications
import com.example.notfallapp.server.ServerCallAlarm


class TimerHandler {
    companion object : INotifications {

            const val timerAfterSosWillSend: Long = 5000
            private lateinit var handler: Handler

            fun timerHandler(context: Context){
                // this, when you would like to have the timer in the main thread
                //handler = Handler(Looper.getMainLooper())
                handler = Handler()
                handler.postDelayed({ //Do something after 10000ms

                    // Send alarm to the Server
                    ServerCallAlarm.sendAlarm(context)

                    // Send position to the Server
                    ServerCallAlarm.sendPosition(context)

                    // Create the notification "Alarm was successful"
                    createNotificationSuccessfulAlarm(context)

                    val intent = Intent(context, AlarmSuccessfulActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, intent, null)
                }, timerAfterSosWillSend)
            }

            fun deleteTimer(){
                try{
                    handler.removeCallbacksAndMessages(null)
                } catch (ex: Exception){ }
            }
    }
}