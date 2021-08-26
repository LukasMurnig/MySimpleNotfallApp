package com.example.notfallapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.annotation.Nullable
import com.example.notfallapp.interfaces.INotifications

/**
 * Foreground Service which send the Notification with the SOS Button
 */
class ForegroundServiceCreateSOSButton : Service(), INotifications {

    companion object{
        /**
         * function that start the ForegroundService
         */
        fun startForegroundService(context: Context){
            val intent = Intent(context, ForegroundServiceCreateSOSButton::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        var sosButtonShown = true
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
        val handler = Handler()
        handler.postDelayed({
            startForegroundService(applicationContext)
        }, 900000)
        super.onStart(intent, startId)
    }

    override fun onLowMemory() {
        val intent = Intent()
        intent.action = "com.notfallApp.service.ReceiverNotificationClose"
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        stopForeground(true)
        val intent = Intent()
        intent.action = "com.notfallApp.service.ReceiverNotificationClose"
        sendBroadcast(intent)
    }
}