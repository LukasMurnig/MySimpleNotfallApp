package com.example.notfallapp.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notfallapp.MainActivity
import com.example.notfallapp.menubar.AlarmsActivity

class BootUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.action)){
            val i  =Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }
}