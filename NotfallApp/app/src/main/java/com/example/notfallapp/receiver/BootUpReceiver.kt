package com.example.notfallapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.Toast
import com.example.notfallapp.MainActivity
import com.example.notfallapp.interfaces.IConnectBracelet
import com.example.notfallapp.service.ServiceCallAlarm

class BootUpReceiver : BroadcastReceiver() {

    // should open MainActivity, when device is finished with boot up
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "HELLO BOOST", Toast.LENGTH_LONG).show()
        if (context != null && intent !=null){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                Toast.makeText(context, "HELLO WE Boot UP", Toast.LENGTH_LONG).show()
                var intent = Intent(context, ServiceCallAlarm::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startService(intent)
                if(prefs.getBoolean("switch", true)){
                    var intent = Intent(context, ServiceCallAlarm::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context?.startService(intent)
                    /*val i = Intent(context, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(i)*/
                }
        }
    }

}