package com.example.notfallapp.connectBracelet

import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.notfallapp.broadcastReciever.ActionsBracelet
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallappLibrary.interfaces.VALRTIBracelet

/**
 * Class for the whole functionality of the V.Alrt bracelet
 */
class BraceletSettings: Activity(), ICreatingOnClickListener, ICheckPermission, VALRTIBracelet {

    lateinit var backBtn: Button
    lateinit var tvStatus: TextView
    lateinit var tvBattery: TextView
    lateinit var btnPair: Button
    lateinit var btnAddNewBracelet: Button
    lateinit var btnDisconnect: Button
    private var device: BluetoothDevice? = null
    private var notificationDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bracelet_settings)

        initComponents()

        initializeListener()

        setVAlrtInformation()
    }

    /**
     * Function for the initialization of the necessary components
     */
    private fun initComponents(){
        backBtn = findViewById(R.id.backBtn)
        tvStatus = findViewById(R.id.tvStatus)
        tvBattery = findViewById(R.id.tvBattery)
        btnPair = findViewById(R.id.btnPair)
        btnAddNewBracelet = findViewById(R.id.btnAddNewBracelet)
        btnDisconnect = findViewById(R.id.btnDisconnect)
    }

    /**
     * Function for the initialization of the necessary listeners
     */
    private fun initializeListener(){
        backBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        btnPair.setOnClickListener {
            val success = device?.let { valrtSelectDevice(this, it) }
            if (success == null) showUserNotification(0)
        }

        btnAddNewBracelet.setOnClickListener {
            val intent = Intent(applicationContext, AddBraceletActivity::class.java)
            startActivity(intent)
        }

        btnDisconnect.setOnClickListener {

        }
    }

    private fun checkVAlrt(connected: Boolean){
        if (!connected){
            btnDisconnect.visibility = View.GONE
            if (btnPair.visibility == View.GONE) btnPair.visibility = View.VISIBLE
        }else{
            btnPair.visibility = View.GONE
            if (btnDisconnect.visibility == View.GONE) btnDisconnect.visibility = View.VISIBLE
        }
    }

    private fun setVAlrtInformation(){
        var battery: Int = ActionsBracelet.batteryState
        var connected: Boolean = ActionsBracelet.connected

        if (!connected){
            tvStatus.compoundDrawables[0].setTint(resources.getColor(R.color.colorRed))
            tvStatus.text = resources.getText(R.string.braceletStatus)
            tvBattery.visibility = View.GONE
        }else{
            tvStatus.compoundDrawables[0].setTint(resources.getColor(R.color.colorGreen))
            tvStatus.text = resources.getText(R.string.connected)
            if (battery <= 100 && battery > 60) tvBattery.compoundDrawables[0].setTint(resources.getColor(R.color.colorGreen))
            if (battery <= 60 && battery > 20) tvBattery.compoundDrawables[0].setTint(resources.getColor(R.color.vAlrtBatteryYellow))
            if (battery <=20) tvBattery.compoundDrawables[0].setTint(resources.getColor(R.color.colorRed))
            tvBattery.text = "$battery%"
            if (tvBattery.visibility == View.GONE) tvBattery.visibility = View.VISIBLE
        }
        checkVAlrt(connected)
    }

    private fun showUserNotification(case: Int){
        notificationDialog = Dialog(this)
        notificationDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        notificationDialog!!.setCancelable(false)
        notificationDialog!!.setContentView(R.layout.notification_dialog_user)
        var textview: TextView = notificationDialog!!.findViewById(R.id.text_dialog)
        textview.text = textSelection(case)
        var okButton: Button = notificationDialog!!.findViewById(R.id.btn_notification_ok)
        okButton.setOnClickListener {
            notificationDialog!!.dismiss()
        }
        notificationDialog!!.show();
    }

    private fun textSelection(case: Int): String{
        var text: String = ""
        when (case) {
            0 -> text = resources.getString(R.string.noDeviceToPair)
        }
        return text
    }
}
