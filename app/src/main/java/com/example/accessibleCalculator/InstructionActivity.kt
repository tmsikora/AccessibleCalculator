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
        return "1. Wykonuj proste operacje matematyczne za pomocą kalkulatora.\n" +
                "2. Używaj gestów dotykowych do wprowadzania liczb i operacji.\n" +
                "3. Słuchaj informacji zwrotnej w formie mówionej dla dostępności.\n\n" +
                "Stuknij w dowolne miejsce na ekranie lub poczekaj 10 sekund, aby kontynuować."
    }

    private fun navigateToNumberInput() {
        // Navigate to the NumberInputActivity
        val intent = Intent(this, MainActivity::class.java)
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
