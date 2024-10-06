package com.masters.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class RegisterActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize ViewModel
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.initializeSharedPreferences(this)

        val usernameEditText = findViewById<EditText>(R.id.register_username)
        val passwordEditText = findViewById<EditText>(R.id.register_password)
        val emailEditText = findViewById<EditText>(R.id.register_confirm_password)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            loginViewModel.register(username, password,
                onSuccess = {
                    // Show success message and navigate to LoginActivity
                    Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Close RegisterActivity
                },
                onError = { errorMessage ->
                    // Show error message to the user
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            )
        }

        // Handle login redirection (when user already has an account)
        val loginRedirect = findViewById<TextView>(R.id.login_redirect)
        loginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close RegisterActivity
        }
    }
}