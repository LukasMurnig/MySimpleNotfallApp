package com.example.notfallapp

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import java.util.concurrent.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.ebanx.swipebtn.SwipeButton
import com.example.notfallapp.bookOnOff.BookOnOffService
import com.example.notfallapp.broadcastReciever.ActionsBracelet
import com.example.notfallapp.checkpoint.CheckpointActivity
import com.example.notfallapp.connectBracelet.AddBraceletActivity
import com.example.notfallapp.connectBracelet.BraceletSettings
import com.example.notfallapp.connectBracelet.BraceletSettingsConnect
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.INotifications
import com.example.notfallapp.login.LoginActivity
import com.example.notfallapp.menubar.settings.SettingsActivity
import com.example.notfallapp.server.ServerAlarm
import com.example.notfallapp.server.ServerApi
import com.example.notfallapp.server.ServerCallAlarm
import com.example.notfallapp.server.ServerUser
import com.example.notfallapp.service.ForegroundServiceCreateSOSButton
import com.example.notfallapp.service.ServiceCallAlarm
import com.example.notfallapp.service.ServiceStartChecking
import com.example.notfallappLibrary.interfaces.VALRTIBracelet
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * MainActivity/HomeActivity, has the buttons to add a bracelet and give information if a bracelet is connected or not
 */
class MainActivity : AppCompatActivity(),
    ICreatingOnClickListener, INotifications, ICheckPermission, VALRTIBracelet {

    companion object {
        var context: Context? = null
        var timer: Timer = Timer()
        var userName: String? =null
        var bookDialog: Dialog? = null
        var bookErrorDialog: Dialog? = null
        var bookOnOffSuccessful: Boolean? = null
        var bookOnOffCase: Int = 0
        var bookOn: Boolean = true
        var checkpointActive: Boolean = false
        var successDialog: Dialog? = null
        var bookOnOffMessage: String = ""
    }
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var message: TextView
    private lateinit var alert_View: ImageView
    private lateinit var cardView: CardView
    private lateinit var lbAlertSlide: TextView
    private lateinit var swipe_btn: SwipeButton
    private lateinit var handler: Handler
    private lateinit var valrt_btn: Button
    private var vAlrtRegistered: Boolean? = null
    private var device: BluetoothDevice? = null
    private val actionsBracelet = ActionsBracelet()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LoginActivity.loggedIn = true
        var s = ServerAlarm()
         s.getActiveAlarm(this)
        initComponents()
        val token = LoginActivity.sharedPreferences?.getString("AccessToken", "")
        Log.e("Token", token)
        try {
            timer = Timer()
            checkState()
        }catch (ex: Exception){
            ex.toString()
        }

        swipe_btn.setOnStateChangeListener() {
            println(it)
            if(it){
                swipe_btn.toggleState()
                val intent = Intent(this, ServiceCallAlarm::class.java)
                startService(intent)
            }
        }

        topAppBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
            val headerView = navigationView.getHeaderView(0)
            val navUsername = headerView.findViewById<View>(R.id.tvUsernameNavigationHeader) as TextView
            navUsername.text = userName.toString()
        }

        setNavigationView()

        checkVAlrt()
        valrt_btn.setOnClickListener {
            if (vAlrtRegistered == false) {
                val intent = Intent(applicationContext, AddBraceletActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(applicationContext, BraceletSettingsConnect::class.java)
                startActivity(intent)
            }
        }

        ServerUser().setUserName(applicationContext)
        val filter = IntentFilter()
        filter.addAction("ACTION_GATT_CONNECTED")
        filter.addAction("ACTION_GATT_DISCONNECTED")
        filter.addAction("Alarm called")
        filter.addAction("BatteryState")
        registerReceiver(actionsBracelet, filter)

        // make server ready
        GlobalScope.launch {
            try{
                ServerApi.getSharedPreferences()
            }catch (ex: UninitializedPropertyAccessException){
                ServerApi.setSharedPreferences(getSharedPreferences("Response", MODE_PRIVATE))
            }

            if(ServerApi.volleyRequestQueue == null){
                ServerApi.volleyRequestQueue = Volley.newRequestQueue(applicationContext)
            }

            if(SettingsActivity.logInUser == null){
                ServerUser().getUserInfo(applicationContext, null, null, null)
            }
        }

        // start the foregroundService which opens the notifcation with the SOS button
        if (!ForegroundServiceCreateSOSButton.sosButtonShown) ForegroundServiceCreateSOSButton.startForegroundService(applicationContext)
        checkGPSPermission()
        /*val beacon = Beacon.Builder()
            .setId1("24d4a05c-3c00-46eb-8bdd-1638165c6b58")
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0000)
            .setTxPower(-59)
            .setDataFields(Arrays.asList(*arrayOf(0L)))
            .build()
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        val btManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val btAdapter = btManager.adapter
        val isSupported = btAdapter.isMultipleAdvertisementSupported
        Log.e("Beacon-Supported", "" + isSupported)

        try {
            val result =
                BeaconTransmitter.checkTransmissionSupported(context)
            Log.e("Beacon-Result", "" + result)
            val beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)
            beaconTransmitter.startAdvertising(beacon)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }*/
    }

    /**
     * Setter for the navigationView and the functionality of the view.
     */
    private fun setNavigationView(){
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            val title = menuItem.title
            val dataprotection = resources.getString(R.string.dataProtection)
            val personDown = resources.getString(R.string.personDownRegister)
            val bookOn = resources.getString(R.string.bookOnRegister)
            var bookOff = resources.getString(R.string.bookOffRegister)
            val braceletRegister = resources.getString(R.string.braceletRegister)
            val checkpoint = resources.getText(R.string.checkpoint)
            val logout = resources.getString(R.string.logout)
            when(title.toString()) {
                braceletRegister -> {
                    if (vAlrtRegistered == false) {
                        val intent = Intent(applicationContext, BraceletSettingsConnect::class.java)
                        startActivity(intent)
                    }else {
                        val intent = Intent(applicationContext, BraceletSettings::class.java)
                        startActivity(intent)
                    }
                }
                checkpoint -> {
                    val intent = Intent(applicationContext, CheckpointActivity::class.java)
                    startActivity(intent)
                }
                bookOn -> {
                    startBookFunction(true)
                }
                bookOff -> {
                    startBookFunction(false)
                }
                dataprotection -> {
                    val url =
                        "https://www.safemotion.info/de/datenschutz/"
                    try {
                        val i = Intent("android.intent.action.MAIN")
                        i.component =
                            ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main")
                        i.addCategory("android.intent.category.LAUNCHER")
                        i.data = Uri.parse(url)
                        startActivity(i)
                    } catch (e: ActivityNotFoundException) {
                        // Chrome is not installed
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(i)
                    }
                    return@setNavigationItemSelectedListener true
                }
                logout -> {
                    ServerApi.getSharedPreferences().edit().clear().commit()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawer(navigationView)
            true
        }
    }

    private fun startBookFunction(bookOn: Boolean){
        MainActivity.bookOn = bookOn
        createBookDialog(bookOn)
        var sb = BookOnOffService()
        sb.startGPSScan(context!!)
    }

    fun createSuccessDialog(){
        bookDialog?.dismiss()
        successDialog = Dialog(context!!)
        successDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        successDialog?.setCancelable(false)
        successDialog?.setContentView(R.layout.dialog_success)
        var textview: TextView? = successDialog?.findViewById(R.id.tv_success_dialog)
        var text = ""
        if (checkpointActive) text = resources.getString(R.string.checkpoint)
        text = if (bookOn) resources.getString(R.string.BookOn)
        else resources.getString(R.string.BookOff)
        textview?.text = String.format(resources.getString(R.string.success),text)
        var okButton: Button? = successDialog?.findViewById(R.id.btn_ok)
        okButton?.setOnClickListener {
            successDialog?.dismiss()
        }
        dismissSuccessDialog()
        successDialog?.show()
    }

    private fun dismissSuccessDialog(){
        Timer().schedule(object : TimerTask(){
            override fun run() {
                successDialog?.dismiss()
            }
        },5000)
    }

    private fun createBookDialog(bookOn: Boolean){
        bookDialog = Dialog(this)
        bookDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bookDialog!!.setCancelable(false)
        bookDialog!!.setContentView(R.layout.book_dialog)
        var textview: TextView = bookDialog!!.findViewById(R.id.tv_book_dialog)
        var text = ""
        if (bookOn) text = resources.getString(R.string.BookOn)
        else text = resources.getString(R.string.BookOff)
        textview.text = String.format(resources.getString(R.string.BookOnOffDialog),text)
        var cancelButton: Button = bookDialog!!.findViewById(R.id.btn_cancel)
        cancelButton.setOnClickListener {
            bookDialog!!.dismiss()
            BookOnOffService().stopScan()
        }
         bookDialog!!.show()
    }

    private fun createBookDialogError(status: Int, bookOn: Boolean) {
        bookDialog?.dismiss()
        bookErrorDialog = Dialog(context!!)
        bookErrorDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bookErrorDialog!!.setCancelable(false)
        bookErrorDialog!!.setContentView(R.layout.book_dialog_error)
        var textView: TextView = bookErrorDialog!!.findViewById(R.id.text_dialog_book_error)
        var text = ""
        when (status) {
            0 -> text = resources.getString(R.string.book_no_Position)
            1 -> text = resources.getString(R.string.book_wrong_Position)
            2 -> text = resources.getString(R.string.book_inaccurate_Position)
        }
        textView.text = String.format(resources.getString(R.string.book_error), text)
        var reSearch: Button = bookErrorDialog!!.findViewById(R.id.btn_dialog_positive)
        var cancel: Button = bookErrorDialog!!.findViewById(R.id.btn_dialog_negative)
        reSearch.setOnClickListener {
            bookErrorDialog!!.dismiss()
            startBookFunction(bookOn)
        }
        cancel.setOnClickListener {
            bookErrorDialog!!.dismiss()
        }
        bookErrorDialog!!.show()
    }

    private fun checkVAlrt() {
        if(vAlrtRegistered == null){
            val success = device?.let { valrtSelectDevice(this, it) }
            if (success == null){
                vAlrtRegistered = false
            }else{
                vAlrtRegistered = true
            }
        }

        if(vAlrtRegistered == true){
            valrt_btn.background = resources.getDrawable(R.drawable.watch)
            if (ActionsBracelet.connected){
                valrt_btn.setBackgroundColor(resources.getColor(R.color.colorGreen))
            }else{
                valrt_btn.setBackgroundColor(resources.getColor(R.color.colorRed))
            }
            valrt_btn.width = 75
        }
    }
    /**
     * try to get the permission of the user for GPS
     */
    private fun checkGPSPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Log.i(
                            resources.getString(R.string.userpermission),
                            resources.getString(R.string.GPSPermissionGranted)
                        )
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(applicationContext, "Some Error! ", Toast.LENGTH_SHORT)
                    .show()
            }
            .onSameThread()
            .check()
    }

    /**
     * Function for
     */

    private fun initComponents() {
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        topAppBar = findViewById(R.id.topAppBar)
        message = findViewById(R.id.message)
        alert_View = findViewById(R.id.alert_View)
        cardView = findViewById(R.id.card_View)
        lbAlertSlide = findViewById(R.id.lbAlertSlide)
        swipe_btn = findViewById(R.id.swipe_btn)
        valrt_btn = findViewById(R.id.vAlrtButton)
        handler = Handler(this.mainLooper)
        context = this
        checkInternetGPSPermissions(this)
        /*if(count == 0) {
            checkBluetoothPermission(this)
            count++
        }*/
        val intent = Intent(this, ServiceStartChecking::class.java)
        context?.startService(intent)
    }

    private fun checkState(){
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    //checkConnected()
                }
            }
        }, 0, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(actionsBracelet)
    }
}

