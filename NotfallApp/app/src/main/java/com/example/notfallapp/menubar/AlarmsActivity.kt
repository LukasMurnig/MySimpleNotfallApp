package com.example.notfallapp.menubar

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.notfallapp.R
import com.example.notfallapp.adapter.AlarmsListAdapter
import com.example.notfallapp.bll.Alarm
import com.example.notfallapp.database.AlarmDatabase
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

    private lateinit var rvAlarms: RecyclerView
    private lateinit var lbMessageNoAlarms: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)

        configureButtons()

        // fill ListView with Alerts
        rvAlarms = findViewById(R.id.rvAlarms)
        rvAlarms.setHasFixedSize(false)
        rvAlarms.layoutManager = LinearLayoutManager(this)
        lbMessageNoAlarms = findViewById(R.id.lbMessageNoAlarms)

        getAlarms()
    }

    private fun getAlarms(){
        val dbclient = DatabaseClient(this)
        val db = dbclient.getAppDatabase(this)


        // get Database data from Alarms
        GlobalScope.launch {
            val data: List<Alarm>? = db?.alarmsDao()?.getAllAlarms()
            if (data != null) {
                if(data.isEmpty()) {
                    lbMessageNoAlarms.text = resources.getString(R.string.noAlarms)
                    return@launch
                }
                setAdapter(data)
            }
        }
    }

    private fun getData(){
        // val db = Room.databaseBuilder(applicationContext, AlarmDatabase::class.java, "alarms.db").build()
        // oder
        // val db2 = AlarmDatabase(this)
        class GetData : AsyncTask<Unit, Unit, List<Alarm>>() {

            override fun doInBackground(vararg p0: Unit?): List<Alarm> {
                val db = AlarmDatabase(this@AlarmsActivity)
                val alarms: List<Alarm> = db.alarmsDao().getAllAlarms()
                return alarms
            }

            override fun onPostExecute(result: List<Alarm>?) {

            }
        }

        val gd = GetData()
        gd.execute()
    }

    private fun setAdapter(data: List<Alarm>){
        val array: ArrayList<Alarm> = ArrayList()
        for(a in data){
            array.add(a)
        }
        runOnUiThread{
            val adapter = AlarmsListAdapter(this, array)

            lvAlarms.adapter = adapter
        }
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