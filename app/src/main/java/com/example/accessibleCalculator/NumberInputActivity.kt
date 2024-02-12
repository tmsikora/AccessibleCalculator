package com.example.accessibleCalculator

import DataHolder
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.annotation.RequiresApi

class NumberInputActivity : ComponentActivity() {

    private var currentNumber: Int = 0
    private lateinit var numberTextView: TextView
    private lateinit var acceptButton: Button
    private lateinit var vibrator: Vibrator
    private lateinit var clickSoundPlayer: MediaPlayer

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_input)

        TextToSpeechManager.initialize(this, "Wybierz liczbę. $currentNumber")
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        clickSoundPlayer = MediaPlayer.create(this, R.raw.click_sound)

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
                    vibrate(50)
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
            vibrate(400)    // Vibrate for 400 milliseconds
            playClickSound()
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

    override fun onDestroy() {
        // Shutdown the text-to-speech engine when the activity is destroyed
        TextToSpeechManager.shutdown()
        clickSoundPlayer.release()
        super.onDestroy()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun vibrate(milliseconds: Long) {
        val vibrationEffect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }

    private fun playClickSound() {
        clickSoundPlayer.start()
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

    private fun updateNumberTextView() {
        numberTextView.text = currentNumber.toString()
    }

    private fun addNumberToEquation() {
        // Assuming DataHolder is a singleton class with a shared equation string
        DataHolder.getInstance().currentEquation += currentNumber.toString()
    }
}
