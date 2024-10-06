package com.masters.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Your layout XML file

        // Initialize ViewModel
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.initializeSharedPreferences(this)

        val usernameEditText = findViewById<EditText>(R.id.login_username)
        val passwordEditText = findViewById<EditText>(R.id.login_password)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            loginViewModel.login(username, password,
                onSuccess = {
                    // Navigate to MainActivity on success
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close the LoginActivity
                },
                onError = { errorMessage ->
                    // Show error message to the user
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            )
        }

        val register_redirect = findViewById<TextView>(R.id.register_redirect)
        register_redirect.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish() // Close RegisterActivity
        }
    }
}