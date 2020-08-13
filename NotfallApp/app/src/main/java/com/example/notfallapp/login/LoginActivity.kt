package com.example.notfallapp.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import com.example.notfallapp.MainActivity
import com.example.notfallapp.R
import com.example.notfallapp.interfaces.ICheckPermission
import com.example.notfallapp.server.ServerApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Activity shows the login screen with two edit text fields (username/email and password) and
 * a button login
 */
class LoginActivity : AppCompatActivity(), ICheckPermission {

    companion object{
        private const val TAG :String = "LoginActivity"
        lateinit var errorLogin: TextView
        var sharedPreferences: SharedPreferences? = null
    }

    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("Response", Context.MODE_PRIVATE)
        ServerApi.setSharedPreferences(getSharedPreferences("Response", Context.MODE_PRIVATE))

        val token = sharedPreferences?.getString("AccessToken", "null")

        if(!token.equals("null")){
            val valid = sharedPreferences?.getLong("TokenValid", 0)
            val unixTime = System.currentTimeMillis() / 1000L

            if(ServerApi.volleyRequestQueue == null){
                ServerApi.volleyRequestQueue = Volley.newRequestQueue(applicationContext)
            }

            if(unixTime > valid!!){
                GlobalScope.launch {
                    ServerApi.refreshToken()
                }
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return
        }
        //a function which findViewById for our Controls in the LoginActivity
        setLoginControls()

        loginButton.setOnClickListener{
            //Function where we login to the server
            login()
        }

    }

    /**
     * check the text fiels and login the user
     */
    private fun login() {
        if (!validate()){
            onLoginFailed()
            return
        }

        loginButton.isEnabled = true

        val username: String? = usernameText.text.toString()
        val password: String? = passwordText.text.toString()

        var code = 0
        // was set before: val sharedPreferences = getSharedPreferences("Response", Context.MODE_PRIVATE)
        ServerApi.setContext(applicationContext)
        // was set before: ServerApi.setSharedPreferences(sharedPreferences)
        if (username != null && password != null) {
            val handler = Handler()
            handler.post {
                ServerApi.sendLogInDataToServer(username, password, this)
            }
        }
    }

    private fun onLoginFailed() {
        Toast.makeText(baseContext, resources.getString(R.string.LoginFailed), Toast.LENGTH_LONG).show()
        loginButton.isEnabled = true
    }

    /**
     * check if the user tip in valid values
     */
    private fun validate(): Boolean {
        var validate = true

        val username: String? = usernameText.text.toString()
        val password: String? = passwordText.text.toString()

        if (username?.isEmpty()!!) {
            usernameText.error = resources.getString(R.string.emailTooShort)
            validate = false
        }else{
            usernameText.error = null
        }

        if (password?.isEmpty()!! || password.length < 4) {
            passwordText.error = resources.getString(R.string.passwordTooShort)
            validate = false
        }else{
            passwordText.error = null
        }

        return validate
    }

    private fun setLoginControls() {
        usernameText = findViewById(R.id.input_username)
        passwordText = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.btn_login)
        errorLogin = findViewById(R.id.error_login)
        checkInternetAccess(this)
    }
}