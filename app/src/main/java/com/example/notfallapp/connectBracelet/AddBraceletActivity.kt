package com.example.notfallapp.connectBracelet

import android.app.Activity
import android.bluetooth.BluetoothAdapter
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
import com.example.notfallapp.interfaces.BeaconInRange
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.server.ServerApi.Companion.TAG
import com.example.notfallappLibrary.interfaces.VALRTIBracelet


class AddBraceletActivity : Activity(), ICreatingOnClickListener, ICheckPermission, VALRTIBracelet {


    private lateinit var btnRetrySearching: Button
    private lateinit var btnCancel: Button
    private lateinit var tvConnectBracelet: TextView
    private lateinit var lvDevices: ListView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var backBtn: Button
    private var devices = ArrayList<BluetoothDevice>()
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_bracelet)

        initComponents()

        initializeListener()
    }

    /**
     * Initialize Components for the Activity and the BroadcastReciever
     */
    private fun initComponents() {
        btnRetrySearching = findViewById(R.id.btn_retry_searching_fab)
        btnCancel = findViewById(R.id.btn_cancel_searching_fab)
        tvConnectBracelet = findViewById(R.id.tvConnectBracelet)
        lvDevices = findViewById(R.id.lvDevices)
        backBtn = findViewById(R.id.backBtn)
        builder = AlertDialog.Builder(this)
        context = this
        var enabled = isBluetoothEnabled(BluetoothAdapter.getDefaultAdapter())
        if (enabled) searchDevice()
        checkPermissions(this)
    }

    /**
     * Initialize the necessary Listener of the buttons and RecyclerView
     */
    private fun initializeListener(){
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

        backBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
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
    }

    /**
     * Function which calls our Libary which look for Bracelet in distance.
     */
    fun searchDevice(){
        BeaconInRange.stopSearchingBeacons()
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