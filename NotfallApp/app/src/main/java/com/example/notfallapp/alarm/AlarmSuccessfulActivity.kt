package com.example.notfallapp.alarm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.MainActivity
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
    private lateinit var buttonSuccessfulOk: Button
    private lateinit var rvSuccessfulAlertLogs: RecyclerView

    companion object{
        var idOfCurrentAlert: Long? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_call_alarm_succesful)
        initComponents()

        buttonSuccessfulOk.setOnClickListener {
            Log.d(resources.getString(R.string.SOSButton),
                  String.format(resources.getString(R.string.SOSButtonClicked),
                                resources.getString(R.string.AlarmSuccessful)))
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        rvSuccessfulAlertLogs = findViewById(R.id.rvAlertLogs)
        rvSuccessfulAlertLogs.setHasFixedSize(false)
        rvSuccessfulAlertLogs.layoutManager = LinearLayoutManager(this)

        startTimer()
    }

    private fun startTimer() {
        Handler().postDelayed({ //Do something after 10000ms
            Log.i("RefreshLog", "Refresh Alert Log")

            MainScope().launch {
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
        buttonSuccessfulOk = findViewById(R.id.btn_alarm_succesful_ok)
        checkInternetGPSPermissions(this)
    }
}