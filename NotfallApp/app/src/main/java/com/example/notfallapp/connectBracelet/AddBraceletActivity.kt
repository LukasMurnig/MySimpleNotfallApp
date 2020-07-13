package com.example.notfallapp.connectBracelet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener

class AddBraceletActivity : AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var btnRetrySearching: Button
    private lateinit var btnCancel: Button
    private lateinit var tvConnectBracelet: TextView
    private lateinit var lvDevices: ListView
    private lateinit var builder: AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_bracelet)

        configureButtons()
        initComponents()

        btnCancel.setOnClickListener() {
            Log.d("ButtonCancel", "Cancel Button was clicked in AddBraceletActivity")
            sureDialog()
            val alert = builder.create()
            alert.show()
        }

        btnRetrySearching.setOnClickListener() {
            Log.d("ButtonSearch", "Search Button was clicked in AddBraceletActivity")
            searchDevices()
        }

        searchDevices()
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

    private fun initComponents() {
        btnRetrySearching = findViewById(R.id.btn_retry_searching)
        btnCancel = findViewById(R.id.btn_cancel_searching_device)
        tvConnectBracelet = findViewById(R.id.tvConnectBracelet)
        lvDevices = findViewById(R.id.lvDevices)
        builder = AlertDialog.Builder(this)
    }

    private fun searchDevices() {
        //TODO search for Bluetooth devices.
    }

    private fun sureDialog() {
        builder.setTitle(getResources().getString(R.string.confirm))
        builder.setMessage(getResources().getString(R.string.sureStopCreatingContact))

        builder.setPositiveButton(getResources().getString(R.string.Yes)) { dialog, which ->
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton(getResources().getString(R.string.No)) {dialog, which ->
            dialog.dismiss()
        }
    }
}