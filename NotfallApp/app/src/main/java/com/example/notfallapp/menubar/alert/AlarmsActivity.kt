package com.example.notfallapp.menubar.alert

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.server.ServerAlarm
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AlarmsActivity : AppCompatActivity(), ICreatingOnClickListener, ICheckPermission {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var rvAlarms: RecyclerView
    private lateinit var lbMessageNoAlarms: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

        // set button bar and sos button
        configureButtons()

        // fill RecyclerView with Alerts
        rvAlarms = findViewById(R.id.rvAlarms)
        rvAlarms.setHasFixedSize(false)
        rvAlarms.layoutManager = LinearLayoutManager(this)
        lbMessageNoAlarms = findViewById(R.id.lbMessageNoAlarms)

        MainScope().launch {
            ServerAlarm().getAllAlerts(applicationContext, rvAlarms, lbMessageNoAlarms)
        }

        /*GlobalScope.launch {
            getData()
        }*/
    }

    private fun configureButtons() {
        // SOS Button
        btnSos = findViewById(R.id.btn_sos)

        // Button bar
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnAlarms.setImageResource(R.drawable.device_active)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnSettings)
        checkInternetGPSPermissions(this)
    }
}