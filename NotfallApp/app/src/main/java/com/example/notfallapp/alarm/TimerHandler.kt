package com.example.notfallapp.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import androidx.core.content.ContextCompat.startActivity


class TimerHandler {
    companion object {
            private lateinit var handler: Handler

            fun timerHandler(context: Context){
                // this, when you would like to have the timer in the main thread
                //handler = Handler(Looper.getMainLooper())
                handler = Handler()
                handler.postDelayed({ //Do something after 10000ms
                    // here must the alarm send to the server

                    val intent = Intent(context, AlarmSuccesfulActivity::class.java)
                    startActivity(context, intent, null)
                }, 10000)
            }

            fun deleteTimer(){
                handler.removeCallbacksAndMessages(null)
            }

    }
}