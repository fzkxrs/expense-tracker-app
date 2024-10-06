package com.masters.app;

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Example: Display some simple statistics
        val statsTextView = findViewById<TextView>(R.id.statsTextView)
        statsTextView.text = "Statistics: 100 Users"
    }
}