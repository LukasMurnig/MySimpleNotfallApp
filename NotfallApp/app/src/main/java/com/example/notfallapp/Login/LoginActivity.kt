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
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.CheckPermission
import com.example.notfallapp.server.ServerApi

class LoginActivity : AppCompatActivity(), CheckPermission {
    companion object{
        private const val TAG :String = "LoginActivity"
        private const val REQUEST_SIGNUP :Int = 0
    }
    private lateinit var usernameText: EditText
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
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        if (!validate()){
            onLoginFailed()
            return
        }

        loginButton.isEnabled = true

        val progressDialog = ProgressDialog(this,
            R.style.ProgressdialogLogin
        )
        progressDialog.isIndeterminate = false
        progressDialog.setMessage(resources.getString(R.string.Authenticate))
        progressDialog.show();

        val username: String? = usernameText.text.toString()
        val password: String? = passwordText.text.toString()

        //Todo: Implementation of the authentication methode.

        ServerApi.setContext(applicationContext)
        if (username != null && password != null) {
            ServerApi.sendLogInDataToServer(username, password)
        }

        /*val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)*/
    }


    private fun onLoginFailed() {
        Toast.makeText(getBaseContext(), resources.getString(R.string.LoginFailed), Toast.LENGTH_LONG).show()
        loginButton.isEnabled = true
    }

    private fun validate(): Boolean {
        var validate = true

        val username: String? = usernameText.text.toString()
        val password: String? = passwordText.text.toString()

        if (username?.isEmpty()!!) {
            usernameText.setError(resources.getString(R.string.emailTooShort))
            validate = false
        }else{
            usernameText.error = null
        }

        if (password?.isEmpty()!! || password?.length < 4) {
            passwordText.setError(resources.getString(R.string.passwordTooShort));
            validate = false;
        }else{
            passwordText.error = null
        }

        return validate
    }

    private fun setLoginControls() {
        usernameText = findViewById(R.id.input_username)
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