package com.example.notfallapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    companion object{
        private val TAG :String = "LoginActivity"
        private val REQUEST_SIGNUP :Int = 0
    }
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var signupText: TextView
    private lateinit var loginButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //a function which findViewById for our Controls in the LoginActivity
        setLoginControls()

        loginButton.setOnClickListener{
            //Function where we login to the server
            login()
        }

        signupText.setOnClickListener{
            //start the signup activity
            var intent: Intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    fun login() {
        if (!validate()){
            onLoginFailed()
            return
        }

        loginButton.setEnabled(true)

        var progressDialog: ProgressDialog = ProgressDialog(this, R.style.ProgressdialogLogin)
        progressDialog.setIndeterminate(true)
        progressDialog.setMessage("Authentifizieren ...")
        progressDialog.show();

        var email: String? = emailText.getText().toString()
        var password: String? = passwordText.getText().toString()

        //Todo: Implementation of the authentication  methode.
    }

    fun onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show()

        loginButton.setEnabled(true)
    }

    fun validate(): Boolean {
        var validate: Boolean = true

        var email: String? = emailText.getText().toString()
        var password: String? = passwordText.getText().toString()

        if (email?.isEmpty()!!) {
            emailText.setError("Feld für Email und Benutzer darf nicht leer sein!")
            validate = false
        }else{
            emailText.setError(null)
        }

        if (password?.isEmpty()!! || password?.length!! < 4) {
            passwordText.setError("Kennwort muss mindestens 4 Zeichen lang sein.");
            validate = false;
        }else{
            passwordText.setError(null)
        }

        return validate
    }

    fun setLoginControls() {
        emailText = findViewById(R.id.input_email)
        passwordText = findViewById(R.id.input_password)
        signupText = findViewById(R.id.link_signup)
        loginButton = findViewById(R.id.btn_login)
    }
}