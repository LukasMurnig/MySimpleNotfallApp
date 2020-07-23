package com.example.notfallapp.connectBracelet

import android.Manifest
import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
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
import com.example.notfallapp.bll.ReadWriteCharacteristic
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.checkPermission
import com.example.notfallapp.service.ServiceCallAlarm


class AddBraceletActivity : Activity(), ICreatingOnClickListener, checkPermission {
    companion object{
        var connected: Boolean = false
        var batteryState: String = " "
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
    private lateinit var context: Context
    private lateinit var characteristic: BluetoothGattCharacteristic
    private lateinit var descriptor: BluetoothGattDescriptor
    private var address = "D0:39:72:C4:FD:DC"
    var process = ProcessQueueExecutor()
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
            //connect(device)
        })
        //To execute the read and write operation in a queue.
       if (!process.isAlive()) {
            process.start()
        }
        connect()
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
        context = this
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        checkPermissions(this, connectivityManager, wifi)
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
        var success = checkPermissions()
        if (success == false){
            return
        }
        this.devices = ArrayList<BluetoothDevice>()
        val adapter = BluetoothListAdapter(applicationContext, devices)
        this.lvDevices.adapter = adapter
        val onFoundFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        onFoundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        onFoundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(mReceiver, onFoundFilter)
        bAdapter.startDiscovery()
        if(bluetoothGatt != null){
            bluetoothGatt!!.disconnect()
            bluetoothGatt!!.close()
        }
    }

    override fun onDestroy() {
        bAdapter.cancelDiscovery()
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    private fun connect(){
        val device: BluetoothDevice =
            BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)
        println("Connnect to device")

        /*characteristic = BluetoothGattCharacteristic(Constants.CLIENT_CHARACTERISTIC_CONFIG, 3, 0)
        val initdescriptor = BluetoothGattDescriptor(Constants.CLIENT_CHARACTERISTIC_CONFIG, 16)
        characteristic.addDescriptor(initdescriptor)
        bluetoothGatt?.setCharacteristicNotification(characteristic, enabled)
        val uuid: UUID = Constants.CLIENT_CHARACTERISTIC_CONFIG
        descriptor = characteristic.getDescriptor(uuid).apply {
            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        }
        bluetoothGatt?.writeDescriptor(descriptor)*/
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun checkPermissions(): Boolean{
        var success = false
        if (bAdapter.isDiscovering) {
            bAdapter.cancelDiscovery()
        }

        val discoverableIntent: Intent =
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            }
        startActivity(discoverableIntent)

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
        success = true
        return success
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