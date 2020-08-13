package com.example.notfallapp.connectBracelet

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
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
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.IConnectBracelet
import com.example.notfallapp.interfaces.ICreatingOnClickListener


class AddBraceletActivity : Activity(), ICreatingOnClickListener, ICheckPermission, IConnectBracelet {

    companion object{
        var connected: Boolean = false
        var batteryState: String = " "
        var device: BluetoothDevice? = null
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

        lvDevices.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            Log.d("ListViewClicked", "List View in Add BraceletActivity was clicked")
            if(devices.size != 0) {
                var device = devices[position]
                connect(this, device, false)
            }else{
                tvConnectBracelet.text = context.getString(R.string.braceleterror)
                val adapter = BluetoothListAdapter(applicationContext, devices)
                lvDevices.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        searchDevices()
        val intent = Intent(this,IConnectBracelet::class.java)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
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

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                    devices = ArrayList<BluetoothDevice>()
                    btnRetrySearching.isClickable = false
                    btnRetrySearching.setTextColor(Color.parseColor("#FF0000"))
                    //discovery starts, we can show progress dialog or perform other tasks
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                    btnRetrySearching.isClickable = true
                    btnRetrySearching.setTextColor(Color.parseColor("#000000"))
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
                        val device =
                            intent.getParcelableExtra<Parcelable>(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice?
                        if (device != null) {
                            if (device.name != null) {
                                if (device.name.contains("V")) {
                                    var exist: Boolean = false
                                    for (indx in devices.indices) {
                                        val arraydevice: BluetoothDevice = devices[indx]
                                        if (arraydevice.address == device.address) {
                                            exist = true;
                                        }
                                    }
                                    if (!exist) {
                                        devices.add(device)
                                    }
                                }
                            }
                        }
                    }catch(ex: Exception){
                        tvConnectBracelet.text = context.getString(R.string.braceleterror)
                    }
                }
            }
        }
    }

    /**
     * Function start BroadCast with searching for devices
     */
    private fun searchDevices() {
        Log.d("SearchDevices", "SearchDevices was called in AddBraceletActivity")
        val success = checkPermissions()
        if (!success){
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

    // Code to manage Service lifecycle.
    /**
     * Function who connect to the Service if the device was initialized
     */
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {

            // Automatically connects to the device upon successful start-up initialization.
            device?.let { connect(application, it, false) }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {

        }
    }

    /**
     * Should stop Discovery and unbindService
     */
    override fun onDestroy() {
        bAdapter.cancelDiscovery()
        unregisterReceiver(mReceiver)
        unbindService(mServiceConnection)
        super.onDestroy()
    }

    /**
     * check if bluetoothAdapeter already discover and if device is discoverable.
     */
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

    /**
     * Function who ask the user if them is sure to stop searching for a bluetooth Device.
     */
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