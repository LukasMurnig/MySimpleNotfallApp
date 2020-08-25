package com.example.notfallapp.alarm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.server.ServerAlarm
import com.example.notfallapp.server.ServerCallAlarm
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Activity shows that the alarm was successful
 **/
class AlarmSuccessfulActivity : AppCompatActivity(), ICheckPermission {

    private lateinit var tvAlarm: TextView
    private lateinit var rvSuccessfulAlertLogs: RecyclerView
    private lateinit var btnEndAlarm: Button

    private lateinit var builder: AlertDialog.Builder

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

        btnEndAlarm.setOnClickListener{
            sureDialog()
            val alert = builder.create()
            alert.show()
        }

        val extras = intent.extras ?: return
        idOfCurrentAlert = extras.getLong("Id")

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
     * Function who ask the user if them is sure to end the alarm
     */
    private fun sureDialog() {
        builder.setTitle(resources.getString(R.string.confirm))
        builder.setMessage(resources.getString(R.string.sureStopSearching))

        builder.setPositiveButton(resources.getString(R.string.Yes)) { dialog, which ->
            ServerCallAlarm.stopAlarm(this, idOfCurrentAlert.toString())
            dialog.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.No)) { dialog, which ->
            dialog.dismiss()
        }
    }

    /**
     * Initialize our Components for the current View
     */
    private fun initComponents() {
        tvAlarm = findViewById(R.id.tvAlarm)
        btnEndAlarm = findViewById(R.id.btn_endAlarm)
        builder = AlertDialog.Builder(this)

        rvSuccessfulAlertLogs = findViewById(R.id.rvSuccessfulAlertLogs)
        rvSuccessfulAlertLogs.setHasFixedSize(false)
        rvSuccessfulAlertLogs.layoutManager = LinearLayoutManager(this)
        checkInternetGPSPermissions(this)
    }
}