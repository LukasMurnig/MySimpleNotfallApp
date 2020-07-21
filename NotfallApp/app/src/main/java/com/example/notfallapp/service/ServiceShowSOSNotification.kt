package com.example.notfallapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.notfallapp.interfaces.INotifications

class ServiceShowSOSNotification: Service(), INotifications {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationCreateAlarm(applicationContext)

        // evtl. Activity wechseln
        return super.onStartCommand(intent, flags, startId)
    }
}