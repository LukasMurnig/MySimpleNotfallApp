package com.example.notfallapp.menubar.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.interfaces.checkPermission
import java.io.File
import java.io.IOException

class SettingsActivity : AppCompatActivity(), ICreatingOnClickListener, checkPermission {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var tvName: TextView
    private lateinit var tvTelNr: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnChangeDate: Button
    private lateinit var imageProfilPicture: ImageView
    private lateinit var btnProfilPicture: ImageButton

    private val IMAGE_DIRECTORY = "/profilPicture"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        initComponents()

        updateProfilPicture()

        tvName.text = "Maria Musterfrau"
        tvTelNr.text = "0123456789"
        tvEmail.text = "maria.musterfrau@mail.com"

        btnChangeDate.setOnClickListener {
            val intent = Intent(this, ChangeProfilActivity::class.java)
            intent.putExtra("name", tvName.text as String)
            intent.putExtra("telNr", tvTelNr.text as String)
            intent.putExtra("email", tvEmail.text as String)
            startActivityForResult(intent, 0)
        }

        btnProfilPicture.setOnClickListener{
            val intent = Intent(this, SelectProfilPictureActivity::class.java)
            startActivityForResult(intent, 2)
        }

        /* to get the setting from somewhere else
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val set = prefs.getBoolean("attachment", true)
        Toast.makeText(this, set.toString(), Toast.LENGTH_LONG).show()*/
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        println(" Result: $resultCode")
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                tvName.text = data.getStringExtra("name")
                tvTelNr.text = data.getStringExtra("telNr")
                tvEmail.text = data.getStringExtra("email")
            }
        }
        if(resultCode == 2){
            updateProfilPicture()
        }
    }

    private fun updateProfilPicture(){
        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY
        )

        try {
            val f = File(
                wallpaperDirectory, "ProfilePicture.jpg"
            )
            if(f.exists()){
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(f.absolutePath, options)
                imageProfilPicture.setImageBitmap(bitmap)
                Log.d("TAG", "File Read::--->" + f.absolutePath)
            }

        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    private fun initComponents(){
        configureButtons()

        tvName = findViewById(R.id.tvName)
        tvTelNr = findViewById(R.id.tvTelNr)
        tvEmail = findViewById(R.id.tvEmail)
        btnChangeDate = findViewById(R.id.btnChangeData)
        imageProfilPicture = findViewById(R.id.imageProfilPicture)
        btnProfilPicture = findViewById(R.id.iBtnProfilPicture)
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        checkInternetAccess(this, connectivityManager, wifi)
    }

    private fun configureButtons() {
        // SOS Button
        btnSos = findViewById(R.id.btn_sos)

        // Button bar
        btnHome = findViewById(R.id.btnHome)
        btnAlarms = findViewById(R.id.btnAlarms)
        btnContact = findViewById(R.id.btnContact)
        btnSettings = findViewById(R.id.btnSettings)
        btnSettings.setImageResource(R.drawable.settings_active)

        createOnClickListener(this, btnSos, btnHome, btnAlarms, btnContact,  btnSettings)
    }
}