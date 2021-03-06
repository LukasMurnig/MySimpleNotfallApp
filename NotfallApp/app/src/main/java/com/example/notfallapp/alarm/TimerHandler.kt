package com.example.notfallapp.alarm

import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.notfallapp.interfaces.INotifications
import com.example.notfallapp.server.ServerCallAlarm

/**
 * TimerHandler to Send our Alarm to the Server after some Time
 */
class TimerHandler {
    companion object : INotifications {

            const val timerAfterSosWillSend: Long = 10000
            private lateinit var handler: Handler
            private var isStopped: Boolean? = null

        /**
         * To start Alarm after some Time if it doesn't get stopped by the user.
         */
        fun timerHandler(context: Context){
                // this, when you would like to have the timer in the main thread
                //handler = Handler(Looper.getMainLooper())
                handler = Handler()
                isStopped = false
                handler.postDelayed({ //Do something after 10000ms

                    if(!isStopped!!){
                        isStopped = true
                        // Send alarm to the Server
                        ServerCallAlarm.sendAlarm(context)

                        // Send position to the Server
                        ServerCallAlarm.sendPosition(context)

                        // Create the notification "Alarm was successful"
                        createNotificationSuccessfulAlarm(context)
                    }
                }, timerAfterSosWillSend)
            }
        /**
         * Delete Timer if the alarm was just an mistake
         */
        fun deleteTimer(){
            Log.i("Timer", "try to stop handler")
            try{
                isStopped = true
                handler.removeCallbacksAndMessages(null)
            }catch (ex: Exception){
                println(ex.toString())
            }
        }
    }
}