package com.example.notfallapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.notfallapp.alarm.CallAlarmActivity
import com.example.notfallapp.alarm.TimerHandler
import com.example.notfallapp.interfaces.INotifications

/**
 * Service create Notification "Alarm will be send soon" with a cancel button
 * and opens the CallAlarmActivity
 */
class ServiceCallAlarm: Service(), INotifications {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags2: Int, startId: Int): Int {
        // create Notification "Alarm will be send soon" with a cancel button
        createNotificationAlarmOnGoing(applicationContext)

        val intentOnSos = Intent(applicationContext, CallAlarmActivity::class.java)
        intentOnSos.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intentOnSos)

        return super.onStartCommand(intent, flags2, startId)
    }
}