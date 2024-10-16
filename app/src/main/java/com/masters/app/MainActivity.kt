package com.masters.app

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.masters.app.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var chooseCategoryButton: Button
    private val categories = arrayOf("General", "Food", "Entertainment", "Travel", "Shopping")
    private var selectedCategoryIndex = 0 // To store the currently selected category
    private lateinit var binding: ActivityMainBinding
    private lateinit var display: TextView
    private var currentNumber = ""
    private var operator = ""
    private var firstOperand = 0.0
    private var secondOperand = 0.0
    private val client = OkHttpClient() // OkHttp client for making network requests
    private var category = "General"
    private var operatorAppended = false // New flag to track if the operator is appended

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve the token from SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

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
        val buttons = listOf(
            R.id.button0,
            R.id.button1,
            R.id.button2,
            R.id.button3,
            R.id.button4,
            R.id.button5,
            R.id.button6,
            R.id.button7,
            R.id.button8,
            R.id.button9
        )
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

        // Initialize the button
        findViewById<Button>(R.id.choose_category_button).setOnClickListener { showCategorySelectionDialog() }

        val circleView = findViewById<CircleView>(R.id.circleView).setNumber(75)
    }


    private fun appendNumber(number: String) {
        currentNumber += number
        display.text = currentNumber
    }

    private fun selectOperator(selectedOperator: String) {
        if (!operatorAppended && currentNumber.isEmpty()) {
            // If no number yet and operator is pressed, append operator and set flag
            operator = selectedOperator
            currentNumber += operator // Append the operator to the number
            operatorAppended = true // Ensure the operator is added only once
            display.text = currentNumber // Update the display to show the operator
        }
    }

    private fun calculateResult() {
        // Only perform the calculation and send if a valid operator and number are entered
        if (currentNumber.isNotEmpty() && (operator == "+" || operator == "-")) {
            try {
                // Parse the full expression (number with the operator)
                val result =
                    currentNumber.toDouble() // This will handle both positive and negative numbers

                // Show the result (the same number in this case, as no operation was done)
                display.text = result.toString()

                // Send result to the remote server
                sendSumToRemoteHost(result)
            } catch (e: NumberFormatException) {
                display.text = "Error"
            }
        }
    }

    private fun clear() {
        currentNumber = ""
        firstOperand = 0.0
        secondOperand = 0.0
        operator = ""
        operatorAppended = false // Reset the operator appended flag
        display.text = "0"
    }

    // Function to send sum result to the remote host
    private fun sendSumToRemoteHost(sum: Double) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", "") // Retrieve the token
        val category = category
        val url = "http://10.0.2.2:8080/api/sum/"
        val timestamp = System.currentTimeMillis()

        // JSON payload with sum, timestamp, category, and token
        val json = """{
        "sum": $sum,
        "timestamp": $timestamp,
        "category": "$category"
    }"""

        val requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $token") // Add the token to the request
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "Failed to send sum: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d(
                        "MainActivity",
                        "Sum sent successfully: $sum, Timestamp: $timestamp, Category: $category, Token: $token"
                    )
                } else {
                    Log.e("MainActivity", "Failed to send sum, response code: ${response.code()}")
                }
            }
        })
    }

    private fun showCategorySelectionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Category")

        // Display the categories in a single-choice dialog
        builder.setSingleChoiceItems(categories, selectedCategoryIndex) { _, which ->
            selectedCategoryIndex = which // Store the selected category index
        }

        // Set the "OK" button
        builder.setPositiveButton("OK") { _, _ ->
            // Update button text with the selected category
            "Category: ${categories[selectedCategoryIndex]}".also { category = it }
            Toast.makeText(
                this,
                "Selected: ${categories[selectedCategoryIndex]}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Set the "Cancel" button
        builder.setNegativeButton("Cancel", null)

        // Show the dialog
        builder.create().show()
    }

}
