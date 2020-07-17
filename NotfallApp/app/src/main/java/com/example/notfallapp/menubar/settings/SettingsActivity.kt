package com.example.notfallapp.menubar.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICreatingOnClickListener

class SettingsActivity : AppCompatActivity(), ICreatingOnClickListener {

    private lateinit var btnSos: Button
    private lateinit var btnHome: ImageButton
    private lateinit var btnContact: ImageButton
    private lateinit var btnAlarms: ImageButton
    private lateinit var btnSettings: ImageButton

    private lateinit var tvName: TextView
    private lateinit var tvTelNr: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnChangeDate: Button
    private lateinit var spStartAppSettings: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsActivity.SettingsFragment())
            .commit()

        configureButtons()

        tvName = findViewById(R.id.tvName)
        tvTelNr = findViewById(R.id.tvTelNr)
        tvEmail = findViewById(R.id.tvEmail)
        btnChangeDate = findViewById(R.id.btnChangeData)

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
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                tvName.text = data!!.getStringExtra("name")
                tvTelNr.text = data.getStringExtra("telNr")
                tvEmail.text = data.getStringExtra("email")
            }
        }
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