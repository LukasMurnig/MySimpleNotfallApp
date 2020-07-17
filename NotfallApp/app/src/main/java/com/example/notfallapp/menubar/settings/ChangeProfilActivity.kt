package com.example.notfallapp.menubar.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.R

class ChangeProfilActivity: AppCompatActivity() {
    private lateinit var etName : EditText
    private lateinit var etTelNr : EditText
    private lateinit var etEmail : EditText

    private lateinit var btnUpdateProfil : Button
    private lateinit var btnCancelProfil : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profil)

        etName = findViewById(R.id.input_name)
        etTelNr = findViewById(R.id.input_telnr)
        etEmail = findViewById(R.id.input_email)
        btnUpdateProfil = findViewById(R.id.btn_updateProfil)
        btnCancelProfil = findViewById(R.id.btn_cancelProfil)

        val extras = intent.extras ?: return

        etName.setText(extras.getString("name"))
        etTelNr.setText(extras.getString("telNr"))
        etEmail.setText(extras.getString("email"))

        btnUpdateProfil.setOnClickListener{
            updateProfil()
        }

        btnCancelProfil.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, null)
            finish()
        }
    }

    private fun updateProfil() {

        // check if all fields are correct
        if(!valid()){
            println("Falsch eingetragen")
            return
        }
        val intent = Intent()
        etName.text.toString()
        intent.putExtra("name", etName.text.toString())
        intent.putExtra("telNr", etTelNr.text.toString())
        intent.putExtra("email", etEmail.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun valid() : Boolean{
        val name: String? = etName.text.toString()
        val telnr: String? = etTelNr.text.toString()
        val email: String? = etEmail.text.toString()

        var valid = true

        if(name?.isEmpty()!!) {
            etName.error = "Name ist nicht ausgefüllt"
            valid = false
        }

        if(telnr?.isEmpty()!! || !android.util.Patterns.PHONE.matcher(telnr).matches()) {
            etTelNr.error = "Keine gültige Telefon Nummer eingegeben"
            valid = false
        }

        if(email?.isEmpty()!! || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.error = "Keine gültige E-mail eingegeben"
            valid = false
        }

        return valid
    }
}