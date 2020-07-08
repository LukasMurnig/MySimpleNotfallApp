package com.example.notfallapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    // Vorname, Nachname, Geschlecht, Geburtstag, email, tel in der API
    private lateinit var etName : EditText
    private lateinit var etTelNr : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnSignUp : Button
    private lateinit var tvAlreadyMember : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etName = findViewById(R.id.input_name)
        etTelNr = findViewById(R.id.input_telnr)
        etEmail = findViewById(R.id.input_email)
        etPassword = findViewById(R.id.input_password)
        btnSignUp = findViewById(R.id.btn_signup)
        tvAlreadyMember = findViewById(R.id.link_login)

        btnSignUp.setOnClickListener{
            signUp()
        }

        tvAlreadyMember.setOnClickListener {
            changeToLoginActivity()
        }
    }

    private fun signUp() {
        // check if all fields are correct
        if(!valid()){
            println("Falsch eingetragen")
            return
        }

        // btnSignUp.isEnabled=false

        setResult(Activity.RESULT_OK, null)
        finish()
    }

    private fun valid() : Boolean{
        var valid = true

        val name: String? = etName.text.toString()
        val telnr: String? = etTelNr.text.toString()
        val email: String? = etEmail.text.toString()
        val password: String? = etPassword.text.toString()

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

        if(password?.length!! < 6) {
            etPassword.error = "muss mehr als 5 Zeichen haben"
            return false
        }

        return valid
    }

    private fun changeToLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}