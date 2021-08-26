package com.example.notfallapp.alarm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebanx.swipebtn.SwipeButton
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.adapter.AlertLogsListAdapter
import com.example.notfallapp.bll.AlertLog
import com.example.notfallapp.interfaces.CurrentLocation
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.server.ServerAlarm
import com.example.notfallapp.server.ServerCallAlarm
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Activity shows that the alarm was successful
 **/
class AlarmSuccessfulActivity : AppCompatActivity(), ICheckPermission {

    private lateinit var rvAlertLog: RecyclerView
    private lateinit var alertLogs: MutableList<AlertLog>
    private lateinit var adapter: AlertLogsListAdapter
    private lateinit var tvUsername: TextView
    private lateinit var swipe_btn: SwipeButton

    companion object{
        var activeAlarmId: Long? = null
        var handler: Handler? = null
        var isActive: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_alarm_succesful)
        initComponents()

        isActive = true

        alertLogs = mutableListOf<AlertLog>()
        println("SystemTime:"+ createSystemTime())
        alertLogs.add(AlertLog(null, null, createSystemTime(), 50, null, applicationContext.getString(R.string.alertInit)))

        adapter = AlertLogsListAdapter(alertLogs)
        rvAlertLog.adapter = adapter
        adapter.notifyDataSetChanged()

        swipe_btn.setOnStateChangeListener() {
            if(it){
                Log.d(resources.getString(R.string.CancelButton),
                    String.format(resources.getString(R.string.CancelButtonClicked),
                        resources.getString(R.string.CallAlarm)))

                /*if(activeAlarmId!=null){
                    // backend not allowed yet that the user self can stop the alarm
                    swipe_btn.toggleState()
                    //ServerCallAlarm.stopAlarm(applicationContext, activeAlarmId.toString())
                }*/
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        getLocation()
        sendAlert()
        startTimer()
        sendBetterPosition()
    }

    /**
     * check if new logs are avaible every 10s
     */
    private fun startTimer() {
        if(handler == null){
            handler = Handler()
        }
        handler!!.postDelayed({ //Do something after 10000ms
            Log.i("RefreshLog", "Refresh Alert Log")
            MainScope().launch {
                ServerAlarm().checkAlarmActive(applicationContext, handler)
                /*if(activeAlarmId!=null){
                    //ServerAlarm().getAlertLogs(activeAlarmId!!, adapter)
                }*/
            }

            startTimer()
        }, 10000) //every 10s
    }

    /**
     * function send Alert and position to backend
     */
    private fun sendAlert(){
        alertLogs.add(AlertLog(null, null, createSystemTime(), 50, null, applicationContext.getString(R.string.alertTransferredInc)))
        adapter.notifyDataSetChanged()

        // Send alarm to the Server
        ServerCallAlarm.sendAlarm(applicationContext)

        // Send position to the Server, create a new location
        ServerCallAlarm.sendPosition(applicationContext)
    }

    /**
     * function get the current location of the user and create a map to see the location
     */
    private fun getLocation(){
        CurrentLocation.getCurrentLocation(this)
        alertLogs.add(AlertLog(null, null, createSystemTime(), 51, null, applicationContext.getString(R.string.positionLocated)))
        adapter.notifyDataSetChanged()
    }

    // create current Time in a similar format like backend
    private fun getSystemTime(): String{
        return "xT"+ Calendar.getInstance().time.hours.toString() +":"+ Calendar.getInstance().time.minutes.toString()+".x"
    }

    private fun createSystemTime(): String{
        var hour: Int = Calendar.getInstance().time.hours+2
        var checkminute: Int = Calendar.getInstance().time.minutes
        var minute: String = checkminute.toString()
        if(checkminute<10) minute = "0$checkminute"
        return "xT$hour:$minute.x"
    }

    private fun initComponents() {
        rvAlertLog = findViewById(R.id.rvAlertLogs)
        rvAlertLog.setHasFixedSize(false)
        rvAlertLog.layoutManager = LinearLayoutManager(this)
        tvUsername = findViewById(R.id.username)
        tvUsername.text = MainActivity.userName.toString()
        swipe_btn = findViewById(R.id.swipe_btn)
    }

    private fun sendBetterPosition(){
        var location = CurrentLocation.currentLocation
        if (location?.accuracy!! > 50){
            CurrentLocation.getCurrentLocation(this)
            // Send position to the Server
            ServerCallAlarm.sendPosition(this)
        }
    }
}