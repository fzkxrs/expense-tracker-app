package com.masters.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.masters.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var display: TextView
    private var currentNumber = ""
    private var operator = ""
    private var firstOperand = 0.0
    private var secondOperand = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve the token from SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        val circleView = findViewById<CircleView>(R.id.circleView)
        circleView.setNumber(75) // Set any number from 0 to 100

        if (token == null) {
            // No token, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity so it doesn't stay in the backstack
            return
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If the token exists, proceed with the main logic
        Log.d("MainActivity", "Token retrieved: $token")

        // Initialize display and button logic
        display = findViewById(R.id.display)

        // Number buttons
        val buttons = listOf(R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9)
        for (id in buttons) {
            findViewById<Button>(id).setOnClickListener { appendNumber((it as Button).text.toString()) }
        }

        // Operator buttons
        findViewById<Button>(R.id.buttonPlus).setOnClickListener { selectOperator("+") }
        findViewById<Button>(R.id.buttonMinus).setOnClickListener { selectOperator("-") }

        // Equals and clear buttons
        findViewById<Button>(R.id.buttonEquals).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.buttonClear).setOnClickListener { clear() }

        // Logout button logic
        findViewById<Button>(R.id.button_logout).setOnClickListener {
            // Clear the auth token from SharedPreferences
            sharedPreferences.edit().remove("auth_token").apply()

            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity so it doesn't stay in the backstack
        }
    }

    private fun appendNumber(number: String) {
        currentNumber += number
        display.text = currentNumber
    }

    private fun selectOperator(selectedOperator: String) {
        if (currentNumber.isNotEmpty()) {
            firstOperand = currentNumber.toDouble()
            currentNumber = ""
            operator = selectedOperator
        }
    }

    private fun calculateResult() {
        if (operator.isNotEmpty() && currentNumber.isNotEmpty()) {
            secondOperand = currentNumber.toDouble()
            val result = when (operator) {
                "+" -> firstOperand + secondOperand
                "-" -> firstOperand - secondOperand
                "*" -> firstOperand * secondOperand
                "/" -> firstOperand / secondOperand
                else -> 0.0
            }
            display.text = result.toString()
            currentNumber = result.toString()
        }
    }

    private fun clear() {
        currentNumber = ""
        firstOperand = 0.0
        secondOperand = 0.0
        operator = ""
        display.text = "0"
    }
}
