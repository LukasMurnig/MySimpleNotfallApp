package com.example.notfallapp.alarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
/**
 * Activity shows that the alarm was successful
 **/
class AlarmSuccessfulActivity : AppCompatActivity(), ICheckPermission {

    private lateinit var tvAlarm: TextView
    private lateinit var buttonSuccessfulOk: Button

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