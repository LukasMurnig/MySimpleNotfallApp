package com.example.notfallapp.menubar.settings

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.bll.User
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.interfaces.ICreatingOnClickListener
import com.example.notfallapp.login.LoginActivity
import com.example.notfallapp.server.ServerApi
import com.example.notfallapp.server.ServerUser
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

/**
 * Activity shows user info of the logged in user, possibly a profile picture if one is selected, and
 * there is a logout button
 */
class SettingsActivity : AppCompatActivity(), ICreatingOnClickListener, ICheckPermission {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var tvName: TextView
    private lateinit var tvTelNr: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout : Button
    private lateinit var btnProfilePicture: ImageButton

    private val IMAGE_DIRECTORY = "/profilPicture"

    companion object{
        var logInUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initComponents()

        btnLogout.setOnClickListener{
            ServerApi.getSharedPreferences().edit().clear().commit()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        updateProfilePicture()

        MainScope().launch {
            ServerUser().getUserInfo(applicationContext, tvName, tvTelNr, tvEmail)
        }

        btnProfilePicture.setOnClickListener{
            val intent = Intent(this, SelectProfilPictureActivity::class.java)
            startActivityForResult(intent, 2)
        }
    }

    /**
     * search for a profile picture in the image directory and shows it, when exist
     */
    private fun updateProfilePicture(){
        MainScope().launch {
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

                    btnProfilePicture.background = BitmapDrawable(bitmap)
                    Log.d(resources.getString(R.string.ReadFile),
                        String.format(resources.getString(R.string.ReadFilePath), f.absolutePath))
                }

            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
    }

    private fun initComponents(){
        configureButtons()

        tvName = findViewById(R.id.tvName)
        tvTelNr = findViewById(R.id.tvTelNr)
        tvEmail = findViewById(R.id.tvEmail)
        btnProfilePicture = findViewById(R.id.iBtnProfilPicture)
        btnLogout = findViewById(R.id.btn_logout_fab)
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