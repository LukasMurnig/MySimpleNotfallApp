package com.example.notfallapp.alarm

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.core.content.ContextCompat.startActivity

class TimerHandler {
    companion object {
        private lateinit var handler: Handler

        fun timerHandler(context: Context){
            handler = Handler()
            handler.postDelayed({
                val intent = Intent(context, AlarmSuccesfulActivity::class.java)
                startActivity(context, intent, null)
            },10000)
        }

        fun deleteTimer(){
            handler.removeCallbacksAndMessages(null)
        }
    }
}