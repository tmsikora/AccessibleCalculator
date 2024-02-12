package com.example.accessibleCalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import com.example.accessibleCalculator.managers.ClickSoundPlayerManager
import com.example.accessibleCalculator.managers.TextToSpeechManager
import com.example.accessibleCalculator.managers.VibratorManager
import com.example.accessibleCalculator.utils.DataHolder
import com.example.accessibleCalculator.utils.ExitPrompt

class NumberInputActivity : ComponentActivity() {

    private var currentNumber: Int = 0
    private lateinit var numberTextView: TextView
    private lateinit var acceptButton: Button

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_input)

        TextToSpeechManager.initialize(this, "Wybierz liczbę. $currentNumber")
        val vibratorManager = VibratorManager.getInstance(this)
        val clickSoundPlayerManager = ClickSoundPlayerManager.getInstance(this)

        numberTextView = findViewById(R.id.numberTextView)
        acceptButton = findViewById(R.id.acceptButton)

        // Set initial number
        updateNumberTextView()

        // Set a touch listener to increase the number based on the number of fingers pressed
        findViewById<View>(android.R.id.content).setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_POINTER_DOWN -> {
                    // Increment the current number by 1 for each finger press
                    currentNumber++
                    // Update the TextView to reflect the new number
                    updateNumberTextView()
                    // Vibrate for 50 milliseconds
                    vibratorManager.vibrate(50)
                    // Speak the updated number
                    TextToSpeechManager.speak("$currentNumber")
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_POINTER_UP -> {
                    // Do nothing on finger release
                }
            }
            true
        }

        // Set a click listener for the Accept button
        acceptButton.setOnClickListener {
            TextToSpeechManager.shutdown()
            vibratorManager.vibrate(400)    // Vibrate for 400 milliseconds
            clickSoundPlayerManager.playClickSound()
            // Add the current number to the shared equation string
            addNumberToEquation()
            // Navigate to ChooseOperationActivity
            val intent = Intent(this, ChooseOperationActivity::class.java)
            startActivity(intent)
            Log.d("NumberInputActivity", "Current Equation: ${DataHolder.getInstance().currentEquation}")
        }

        onBackPressedDispatcher.addCallback(this) {
            ExitPrompt.showExitPrompt(this@NumberInputActivity)
        }
    }

    override fun onDestroy() {
        // Shutdown the text-to-speech engine when the activity is destroyed
        TextToSpeechManager.shutdown()
        ClickSoundPlayerManager.getInstance(this).release()
        super.onDestroy()
    }

    private fun updateNumberTextView() {
        numberTextView.text = currentNumber.toString()
    }

    private fun addNumberToEquation() {
        // Assuming com.example.accessibleCalculator.utils.DataHolder is a singleton class with a shared equation string
        DataHolder.getInstance().currentEquation += currentNumber.toString()
    }
}
