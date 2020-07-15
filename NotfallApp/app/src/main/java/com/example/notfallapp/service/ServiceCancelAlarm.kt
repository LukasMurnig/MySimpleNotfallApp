package com.example.notfallapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.notfallapp.alarm.AlarmCanceledActivity
import com.example.notfallapp.alarm.CallAlarmActivity
import com.example.notfallapp.alarm.TimerHandler

class ServiceCancelAlarm: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags2: Int, startId: Int): Int {
        TimerHandler.deleteTimer()
        val intentOnSos = Intent(applicationContext, AlarmCanceledActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK and  Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intentOnSos.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intentOnSos)

        return super.onStartCommand(intent, flags2, startId)
    }
}