package com.example.notfallapp.menubar.settings

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.server.ServerUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChangeProfilActivity: AppCompatActivity(), ICheckPermission {
    private lateinit var etName : EditText
    private lateinit var etAddress: EditText
    private lateinit var etTelNr : EditText
    private lateinit var etEmail : EditText

    private lateinit var btnUpdateProfile : Button
    private lateinit var btnCancelProfile : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profil)

        initComponents()

        if(SettingsActivity.logInUser != null){
            etName.setText(SettingsActivity.logInUser!!.forename + " " + SettingsActivity.logInUser!!.surname)
            etEmail.setText(SettingsActivity.logInUser!!.emailAddress)
            etTelNr.setText(SettingsActivity.logInUser!!.phoneFixed)
        }

        btnUpdateProfile.setOnClickListener{
            updateProfil()
        }

        btnCancelProfile.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, null)
            finish()
        }
    }

    private fun updateProfil() {

        // check if all fields are correct
        if(!valid()){
            return
        }

        val loginUser = SettingsActivity.logInUser
        if(loginUser != null){
            loginUser.phoneFixed = etTelNr.text.toString()
            loginUser.emailAddress = etEmail.text.toString()

            GlobalScope.launch {
                ServerUser().updateUserInfo(loginUser)
            }
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun valid() : Boolean{
        val name: String? = etName.text.toString()
        val telnr: String? = etTelNr.text.toString()
        val email: String? = etEmail.text.toString()

        var valid = true

        if(name?.isEmpty()!!) {
            etName.error = resources.getString(R.string.nameNotFilled)
            valid = false
        }

        if(telnr?.isEmpty()!! || !android.util.Patterns.PHONE.matcher(telnr).matches()) {
            etTelNr.error = resources.getString(R.string.noValidnumber)
            valid = false
        }

        if(email?.isEmpty()!! || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.error = resources.getString(R.string.noValidemail)
            valid = false
        }

        return valid
    }

    private fun initComponents(){
        etName = findViewById(R.id.input_name)
        etAddress = findViewById(R.id.input_address)
        etTelNr = findViewById(R.id.input_telnr)
        etEmail = findViewById(R.id.input_email)
        btnUpdateProfile = findViewById(R.id.btn_updateProfil)
        btnCancelProfile = findViewById(R.id.btn_cancelProfil)

        /*val extras = intent.extras ?: return

        etName.setText(extras.getString(resources.getString(R.string.nameSettings)))
        etTelNr.setText(extras.getString(resources.getString(R.string.numberSettings)))
        etEmail.setText(extras.getString(resources.getString(R.string.emailAlarmDatabase)))*/

        checkInternetGPSPermissions(this)
    }
}