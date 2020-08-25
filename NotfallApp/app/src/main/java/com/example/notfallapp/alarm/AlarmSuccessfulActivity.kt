package com.example.notfallapp.alarm

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.server.ServerAlarm
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Activity shows that the alarm was successful
 **/
class AlarmSuccessfulActivity : AppCompatActivity(), ICheckPermission {

    private lateinit var tvAlarm: TextView
    private lateinit var rvSuccessfulAlertLogs: RecyclerView

    companion object{
        var idOfCurrentAlert: Long? = null
        var handler: Handler? = null
        var isActive: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_call_alarm_succesful)
        initComponents()

        isActive = true

        val extras = intent.extras ?: return
        idOfCurrentAlert = extras.getLong("Id")

        rvSuccessfulAlertLogs = findViewById(R.id.rvSuccessfulAlertLogs)
        rvSuccessfulAlertLogs.setHasFixedSize(false)
        rvSuccessfulAlertLogs.layoutManager = LinearLayoutManager(this)

        MainScope().launch {
            ServerAlarm().getAlertLogs(rvSuccessfulAlertLogs, idOfCurrentAlert!!, true)
        }

        startTimer()
    }

    private fun startTimer() {
        if(handler == null){
            handler = Handler()
        }
        handler!!.postDelayed({ //Do something after 10000ms
            Log.i("RefreshLog", "Refresh Alert Log")
            MainScope().launch {
                ServerAlarm().checkAlarmActive(applicationContext, handler)
                ServerAlarm().getAlertLogs(rvSuccessfulAlertLogs, idOfCurrentAlert!!, true)
            }

            startTimer()
        }, 10000)
    }

    /**
     * Initialize our Components for the current View
     */
    private fun initComponents() {
        tvAlarm = findViewById(R.id.tvAlarm)
        checkInternetGPSPermissions(this)
    }
}