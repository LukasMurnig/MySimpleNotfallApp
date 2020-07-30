package com.example.notfallapp.menubar

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notfallapp.R
import com.example.notfallapp.adapter.AlarmsListAdapter
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.bll.AlertingChain
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.ICheckPermission
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmsActivity : AppCompatActivity(), ICreatingOnClickListener, ICheckPermission {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var rvAlarms: RecyclerView
    private lateinit var lbMessageNoAlarms: TextView

    companion object{
        var alertingChain: AlertingChain? = null
    }

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

        GlobalScope.launch {
            getData()

            /*
            ServerAlarm.getAllAlerts(rvAlarms, lbMessageNoAlarms)
             */
        }
    }

    private fun getData(){
        class GetData : AsyncTask<Unit, Unit, List<Alarm>>() {

            override fun doInBackground(vararg p0: Unit?): List<Alarm> {
                val db = EmergencyAppDatabase.getInstance(this@AlarmsActivity)
                return db.alarmsDao().getAllAlarms()
            }

            override fun onPostExecute(result: List<Alarm>?) {
                if(result != null){
                    if(result.isEmpty()){
                        lbMessageNoAlarms.text = resources.getString(R.string.noAlarms)
                    }else{
                        val adapter = AlarmsListAdapter(result)
                        rvAlarms.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        val gd = GetData()
        gd.execute()
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