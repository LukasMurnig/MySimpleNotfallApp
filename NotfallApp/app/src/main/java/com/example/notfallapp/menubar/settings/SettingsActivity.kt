package com.example.notfallapp.menubar.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.notfallapp.R
import com.example.notfallapp.bll.User
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.server.ServerUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class SettingsActivity : AppCompatActivity(), ICreatingOnClickListener, ICheckPermission {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var tvName: TextView
    private lateinit var tvTelNr: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnChangeDate: Button
    private lateinit var btnProfilePicture: ImageButton
    private lateinit var btnLogout: Button

    private val IMAGE_DIRECTORY = "/profilPicture"

    companion object{
        var logInUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        initComponents()

        btnLogout.setOnClickListener{
            Toast.makeText(applicationContext, "Not implemented yet", Toast.LENGTH_LONG).show()
        }

        updateProfilePicture()

        GlobalScope.launch {
            if(logInUser==null){
                ServerUser().getUserInfo(applicationContext)
                while(logInUser==null){

                }
            }

            tvName.text = logInUser!!.forename + " " + logInUser!!.surname
            tvTelNr.text = logInUser!!.phoneFixed
            tvEmail.text = logInUser!!.emailAddress

            /*tvName.text = resources.getString(R.string.sampleName)
            tvTelNr.text = resources.getString(R.string.sampleNumber)
            tvEmail.text = resources.getString(R.string.sampleEmail)*/
        }

        btnChangeDate.setOnClickListener {
            Toast.makeText(applicationContext, "Gerade nicht verfügbar, da nicht sicher ob gebraucht wird", Toast.LENGTH_LONG).show()
            /*val intent = Intent(this, ChangeProfilActivity::class.java)
            intent.putExtra(resources.getString(R.string.numberAlarmDatabas), tvName.text as String)
            intent.putExtra(resources.getString(R.string.telNr), tvTelNr.text as String)
            intent.putExtra(resources.getString(R.string.emailAlarmDatabase), tvEmail.text as String)
            startActivityForResult(intent, 0)*/
        }

        btnProfilePicture.setOnClickListener{
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
            if (resultCode == Activity.RESULT_OK /*&& data != null*/) {
                tvName.text = logInUser!!.forename + " " + logInUser!!.surname
                tvTelNr.text = logInUser!!.phoneFixed
                tvEmail.text = logInUser!!.emailAddress
            }
        }
        if(resultCode == 2){
            updateProfilePicture()
        }
    }

    private fun updateProfilePicture(){
        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY
        )

        try {
            val f = File(
                wallpaperDirectory, resources.getString(R.string.namePicture)
            )
            if(f.exists()){
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(f.absolutePath, options)
                btnProfilePicture.setImageBitmap(bitmap)
                Log.d(resources.getString(R.string.ReadFile),
                      String.format(resources.getString(R.string.ReadFilePath), f.absolutePath))
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
        btnProfilePicture = findViewById(R.id.iBtnProfilPicture)
        btnLogout = findViewById(R.id.btnLogOut)
        checkInternetGPSPermissions(this)
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