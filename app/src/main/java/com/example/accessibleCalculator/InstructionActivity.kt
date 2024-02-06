package com.example.accessibleCalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity

class InstructionActivity : ComponentActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val delayedTimeMillis: Long = 10000 // 10 seconds

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        // Show the instructions
        val instructionsTextView: TextView = findViewById(R.id.instructionsTextView)
        instructionsTextView.text = getInstructionsText()

        // Set a delayed runnable to finish the activity after a certain time
        handler.postDelayed({
            finish()
        }, delayedTimeMillis)

        // Set a touch listener to finish the activity on touch
        findViewById<View>(android.R.id.content).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                navigateToNumberInput()
                //finish()
            }
            true
        }
    }

    private fun getInstructionsText(): String {
        // Customize the instructions text here
        return "1. Perform simple math operations using the calculator.\n" +
                "2. Use touch gestures to input numbers and operations.\n" +
                "3. Listen to the spoken feedback for accessibility.\n\n" +
                "Tap anywhere on the screen or wait for 10 seconds to continue."
    }

    private fun navigateToNumberInput() {
        // Navigate to the NumberInputActivity
        val intent = Intent(this, NumberInputActivity::class.java)
        startActivity(intent)

        // Remove any remaining callbacks to prevent unwanted navigation
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        // Remove the delayed runnable callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
