package com.example.notfallapp.Login

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.checkPermission

class LoginActivity : AppCompatActivity(), checkPermission {
    companion object{
        private val TAG :String = "LoginActivity"
        private const val REQUEST_SIGNUP :Int = 0
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
            val intent: Intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    fun login() {
        if (!validate()){
            onLoginFailed()
            return
        }

        loginButton.setEnabled(true)

        val progressDialog = ProgressDialog(this,
            R.style.ProgressdialogLogin
        )
        progressDialog.setIndeterminate(true)
        progressDialog.setMessage("Authentifizieren ...")
        progressDialog.show();

        var email: String? = emailText.getText().toString()
        var password: String? = passwordText.getText().toString()

        //Todo: Implementation of the authentication  methode.
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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

        if (password?.isEmpty()!! || password?.length < 4) {
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
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi =
            getSystemService(Context.WIFI_SERVICE) as WifiManager
        checkInternetAccess(this, connectivityManager, wifi)
    }
}