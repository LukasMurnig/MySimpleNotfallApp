package com.example.notfallapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        try {
            startForeground(435624234, createNotificationCreateAlarm(applicationContext))
        } catch (ex: Exception){
            ex.printStackTrace()
        }

        return START_STICKY
        //return START_REDELIVER_INTENT
    }

    override fun onStart(intent: Intent?, startId: Int) {
        /*try {
            startForeground(435624234, createNotificationCreateAlarm(applicationContext))
        } catch (ex: Exception){
            ex.printStackTrace()
        }*/
        val handler = Handler()
        handler.postDelayed({
            startForegroundService(applicationContext)
        }, 1800000)
        super.onStart(intent, startId)
    }

    override fun onLowMemory() {
        val intent = Intent()
        intent.action = "com.notfallapp.SOSReceiver"
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        val intent = Intent()
        intent.action = "com.notfallapp.SOSReceiver"
        sendBroadcast(intent)
    }
}