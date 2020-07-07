package com.example.notfallapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var etName : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnSignUp : Button
    private lateinit var tvAlreadyMember : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etName = findViewById(R.id.input_name)
        etEmail = findViewById(R.id.input_email)
        etPassword = findViewById(R.id.input_password)
        btnSignUp = findViewById(R.id.btn_signup)
        tvAlreadyMember = findViewById(R.id.link_login)

        btnSignUp.setOnClickListener{
            if (etName.text != null && etEmail.text != null){
                // TODO: change to a service request
                // wird dann ein Service request:
                val intent = Intent(this, MainActivity::class.java )
                intent.putExtra("name", etName.text)
                intent.putExtra("email", etEmail.text)
                intent.putExtra("password", etPassword.text)
                startActivity(intent)
            }
        }

        tvAlreadyMember.setOnClickListener {
            // TODO: change to login Activity
            throw NotImplementedError()
            val Intent = Intent(this, LoginActivity::class.java)
        }
    }
}