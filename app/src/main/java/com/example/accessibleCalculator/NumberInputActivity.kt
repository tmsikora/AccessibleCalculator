package com.example.accessibleCalculator

import DataHolder
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.addCallback

class NumberInputActivity : ComponentActivity() {

    private var currentNumber: Int = 0
    private lateinit var numberTextView: TextView
    private lateinit var acceptButton: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_input)

        numberTextView = findViewById(R.id.numberTextView)
        acceptButton = findViewById(R.id.acceptButton)

        updateNumberTextView()

        // Set a touch listener to increase the number on tap
        findViewById<View>(android.R.id.content).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                increaseNumber()
                updateNumberTextView()
            }
            true
        }

        // Set a click listener for the Accept button
        acceptButton.setOnClickListener {
            // Add the current number to the shared equation string
            addNumberToEquation()
            // Navigate to ChooseOperationActivity
            val intent = Intent(this, ChooseOperationActivity::class.java)
            startActivity(intent)
            Log.d("NumberInputActivity", "Current Equation: ${DataHolder.getInstance().currentEquation}")
        }

        onBackPressedDispatcher.addCallback(this) {
            showExitPrompt()
        }
    }

    private fun showExitPrompt() {
        // Show your custom prompt or dialog here
        // For example, you can use AlertDialog to display the prompt
        AlertDialog.Builder(this)
            .setMessage("Czy chcesz wrócić do ekranu głównego aplikacji?")
            .setPositiveButton("Tak") { _, _ ->
                DataHolder.getInstance().currentEquation = ""
                // Navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            .setNegativeButton("Nie") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun increaseNumber() {
        currentNumber++
    }

    private fun updateNumberTextView() {
        numberTextView.text = currentNumber.toString()
    }

    private fun addNumberToEquation() {
        // Assuming DataHolder is a singleton class with a shared equation string
        DataHolder.getInstance().currentEquation += currentNumber.toString()
    }
}
