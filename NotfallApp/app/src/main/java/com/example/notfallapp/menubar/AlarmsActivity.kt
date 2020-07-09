package com.example.notfallapp.menubar

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.notfallapp.R
import com.example.notfallapp.adapters.CustomAlarmAdapter
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.database.EmergencyAppDatabase
import com.example.notfallapp.databasemanager.DatabaseManager
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.viewmodel.AlarmsViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class AlarmsActivity : AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var lvAlarms: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

        configureButtons()

        // fill ListView with Alerts
        lvAlarms = findViewById(R.id.lvAlarms)

        getAlarms()
    }

    private fun getAlarms(){
        try {
            val db = Room.databaseBuilder(
                        applicationContext,
                        EmergencyAppDatabase::class.java, "emergency.db"
                    ).build()

            println("Hallo")
            println("$db")

            GlobalScope.launch {
                val data = db.alarmsDao().getAllAlarms()

                setAdapter(data)
            }

        }catch (error: Exception){
            Log.e("DatabaseError", error.toString())

            /*val h = listOf(Alarm("1", "Could not load alerts", "gerade"))
            setAdapter(h)*/
        }
    }

    private fun setAdapter(data: List<Alarm>){
        lvAlarms.adapter = CustomAlarmAdapter(this, data)
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