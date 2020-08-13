package com.example.notfallapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.notfallapp.alarm.AlarmCanceledActivity
import com.example.notfallapp.alarm.TimerHandler
import com.example.notfallapp.interfaces.INotifications

class ServiceCancelAlarm: Service(), INotifications {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags2: Int, startId: Int): Int {
        // stop timer, before the timer can send to the server
        TimerHandler.deleteTimer()
        // because of a bug
        TimerHandler.deleteTimer()

        // create Notification "Alarm was cancelled"
        createNotificationCancelledAlarm(this)

        val intentOnSos = Intent(applicationContext, AlarmCanceledActivity::class.java)
        intentOnSos.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intentOnSos)

        return super.onStartCommand(intent, flags2, startId)
    }
}