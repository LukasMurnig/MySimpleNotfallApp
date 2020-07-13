package com.example.notfallapp.alarm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener

class CallAlarmActivity : AppCompatActivity(){


    private lateinit var btnCancelAlarm: Button
    private lateinit var tvAlarm: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_alarm)

        initComponents()

        var handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, AlarmSuccesfulActivity::class.java)
            startActivity(intent)
        },10000)

        btnCancelAlarm.setOnClickListener() {
            Log.d("CancelButtonClicked", "Cancel Button in CallAlarmActivity clicked")
            handler.removeCallbacksAndMessages(null)
            var intent = Intent(this, AlarmCanceledActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initComponents(){
        btnCancelAlarm = findViewById(R.id.btn_cancel_alarm)
        tvAlarm = findViewById(R.id.tvAlarm)
    }
}