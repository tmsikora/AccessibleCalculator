    package com.example.accessibleCalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.content.Intent

    class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton: Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            // Start the instruction activity
            val intent = Intent(this, InstructionActivity::class.java)
            startActivity(intent)
        }
    }
}