package com.example.notfallapp.menubar

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.adapter.AlarmsListAdapter
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.database.DatabaseClient
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmsActivity : AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var lvAlarms: ListView
    private lateinit var lbMessageNoAlarms: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

        configureButtons()

        // fill ListView with Alerts
        lvAlarms = findViewById(R.id.lvAlarms)
        lbMessageNoAlarms = findViewById(R.id.lbMessageNoAlarms)

        getAlarms()
    }

    private fun getAlarms(){
        val dbclient = DatabaseClient(this)
        val db = dbclient.getAppDatabase(this)
        GlobalScope.launch {
            val data: List<Alarm>? = db?.alarmsDao()?.getAllAlarms()
            if (data != null) {
                if(data.isEmpty()) {
                    lbMessageNoAlarms.text = resources.getString(R.string.noAlarms)
                }else{
                    setAdapter(data)
                }
            }
        }
    }

    private fun setAdapter(data: List<Alarm>){
        val adapter = AlarmsListAdapter(this, data as ArrayList<Alarm>)
        lvAlarms.adapter = adapter
    }

    private fun configureButtons() {
        // SOS Button
        btnSos = findViewById(R.id.btn_sos)

        // Button bar
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact, btnSettings)
    }
}