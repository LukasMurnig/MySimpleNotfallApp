package com.example.notfallapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.Nullable
import com.example.notfallapp.interfaces.INotifications

class ForegroundServiceCreateSOSButton : Service(), INotifications {

    companion object{
        fun startForegroundService(context: Context){
            val intent = Intent(context, ForegroundServiceCreateSOSButton::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    @Nullable
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        try{
            startForeground(444444123, createNotificationCreateAlarm(applicationContext))
        } catch (ex: Exception){
            ex.printStackTrace()
        }

        return START_REDELIVER_INTENT
    }
}