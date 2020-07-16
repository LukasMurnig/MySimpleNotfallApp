package com.example.notfallapp.connectBracelet

import android.Manifest
import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.adapter.BluetoothListAdapter
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import java.util.*
import kotlin.collections.ArrayList


class AddBraceletActivity : Activity(), ICreatingOnClickListener {
    companion object{
        var connected: Boolean = false
    }
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
    private var bAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var mReceiver: BroadcastReceiver
    private var devices = ArrayList<BluetoothDevice>()
    private var bluetoothGatt: BluetoothGatt? = null
    private val STATE_DISCONNECTED = 0
    private val STATE_CONNECTING = 1
    private val STATE_CONNECTED = 2
    val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
    val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
    val ACTION_GATT_SERVICES_DISCOVERED =
        "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
    val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
    val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"
    private var connectionState = STATE_DISCONNECTED
    private lateinit var characteristic: BluetoothGattCharacteristic
    private lateinit var descriptor: BluetoothGattDescriptor
    var enabled: Boolean = true
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
            tvConnectBracelet.text = getResources().getString(R.string.tryToConnectBracelet)
            searchDevices()
        }

        lvDevices.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            Log.d("ListViewClicked", "List View in Add BraceletActivity was clicked")
            val device: BluetoothDevice = devices[position]
            //characteristic = BluetoothGattCharacteristic(UUID.fromString(uuidExtra.toString()), 1, 3)
            connect(device)
        })
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
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                    devices = ArrayList<BluetoothDevice>()
                    //discovery starts, we can show progress dialog or perform other tasks
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                    if(devices.size == 0){
                        tvConnectBracelet.text = getResources().getString(R.string.nobluetoothdevicesfound)
                    }else {
                        val adapter = BluetoothListAdapter(applicationContext, devices)
                        println(adapter.count.toString())
                        lvDevices.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                } else if (BluetoothDevice.ACTION_FOUND == action) {
                    //bluetooth device found
                    try {
                        var device =
                            intent.getParcelableExtra<Parcelable>(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice
                        if (device != null) {
                            if (device.name != null) {
                                if (device.name.contains("V")) {
                                    var exist: Boolean = false
                                    for (indx in devices.indices) {
                                        var arraydevice: BluetoothDevice = devices[indx]
                                        if (arraydevice.address.equals(device.address)) {
                                            exist = true;
                                        }
                                    }
                                    if (exist != true) {
                                        devices.add(device)
                                    }
                                }
                            }
                        }
                    }catch(ex: Exception){
                        tvConnectBracelet.text = getResources().getString(R.string.error)
                    }
                }
            }
        }
    }

    private fun searchDevices() {
        //TODO search for Bluetooth devices.
        Log.d("SearchDevices", "SearchDevices was called in AddBraceletActivity")

        if (!bAdapter.isEnabled) {
            val eintent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(eintent)
        }

        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
        startActivity(discoverableIntent)

        if (bAdapter.isDiscovering) {
            bAdapter.cancelDiscovery()
        }

        /*val pairedDevices = bAdapter.bondedDevices
        if (pairedDevices.size > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (device in pairedDevices) {
                System.out.println(device.address)
            }
        }*/

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            Log.i("info", "No fine location permissions")

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        val onFoundFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        onFoundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        onFoundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(mReceiver, onFoundFilter)
        bAdapter.startDiscovery()
    }

    override fun onDestroy() {
        bAdapter.cancelDiscovery()
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    private fun connect(device: BluetoothDevice){
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        /*bluetoothGatt?.setCharacteristicNotification(characteristic, enabled)
        descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        bluetoothGatt?.writeDescriptor(descriptor)*/
    }

    // Various callback methods defined by the BLE API.
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    println("HEllo Conected")
                    connected = true
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    println("Hello disconected")
                    connected = false
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            broadcastUpdate(ACTION_DATA_AVAILABLE)
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }
    private fun sureDialog() {
        builder.setTitle(getResources().getString(R.string.confirm))
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