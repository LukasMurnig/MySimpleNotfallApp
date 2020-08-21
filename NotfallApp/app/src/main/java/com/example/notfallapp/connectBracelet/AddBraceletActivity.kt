package com.example.notfallapp.connectBracelet

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.adapter.BluetoothListAdapter
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.server.ServerApi.Companion.TAG
import com.example.notfallappLibrary.interfaces.VALRTIBracelet


class AddBraceletActivity : Activity(), ICreatingOnClickListener, ICheckPermission, VALRTIBracelet {

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
    private var devices = ArrayList<BluetoothDevice>()
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_bracelet)

        configureButtons()
        initComponents()

        btnCancel.setOnClickListener {
            Log.d("ButtonCancel", "Cancel Button was clicked in AddBraceletActivity")
            sureDialog()
            val alert = builder.create()
            alert.show()
        }

        btnRetrySearching.setOnClickListener {
            Log.d("ButtonSearch", "Search Button was clicked in AddBraceletActivity")
            tvConnectBracelet.text = resources.getString(R.string.tryToConnectBracelet)
            searchDevice()
        }

        lvDevices.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            Log.d("ListViewClicked", "List View in Add BraceletActivity was clicked")

            if(devices.size != 0) {
                val device = devices[position]

                valrtSelectDevice(this, device)
                valrtConnectToSelectedDevice(this, false, { string ->
                    Log.e(TAG, string)
                    tvConnectBracelet.text = context.getString(R.string.braceleterror)
                },{
                    Log.e(TAG, "SUCCESS")
                    val intent = Intent(this, MainActivity::class.java)
                    this.startActivity(intent)
                })
            }else{
                tvConnectBracelet.text = context.getString(R.string.braceleterror)
                val adapter = BluetoothListAdapter(applicationContext, devices)
                lvDevices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
        searchDevice()
    }

    /**
     * Initialize Button from the MenuBar and SOS Button for the Activity
     */
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

    /**
     * Initialize Components for the Activity and the BroadcastReciever
     */
    private fun initComponents() {
        btnRetrySearching = findViewById(R.id.btn_retry_searching_fab)
        btnCancel = findViewById(R.id.btn_cancel_searching_fab)
        tvConnectBracelet = findViewById(R.id.tvConnectBracelet)
        lvDevices = findViewById(R.id.lvDevices)
        builder = AlertDialog.Builder(this)
        context = this
        checkPermissions(this)
    }

    /**
     * Function which calls our Libary which look for Bracelet in distance.
     */
    private fun searchDevice(){
        btnRetrySearching.isEnabled = false
        valrtScanForDevices(this, { message ->
            tvConnectBracelet.text = this.getString(R.string.nobluetoothdevicesfound)
            btnRetrySearching.isEnabled = true
        }){ devices ->
            val adapter = BluetoothListAdapter(applicationContext, devices)
            this.devices = devices
            lvDevices.adapter = adapter
            adapter.notifyDataSetChanged()
            btnRetrySearching.isEnabled = true
        }
    }

    /**
     * Function who ask the user if them is sure to stop searching for a bluetooth Device.
     */
    private fun sureDialog() {
        builder.setTitle(resources.getString(R.string.confirm))
        builder.setMessage(resources.getString(R.string.sureStopSearching))

        builder.setPositiveButton(getResources().getString(R.string.Yes)) { dialog, which ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton(getResources().getString(R.string.No)) {dialog, which ->
            dialog.dismiss()
        }
    }
}