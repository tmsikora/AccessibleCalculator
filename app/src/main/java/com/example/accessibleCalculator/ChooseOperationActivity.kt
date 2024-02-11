package com.example.accessibleCalculator

import DataHolder
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import java.util.Locale

enum class MathOperation(val symbol: String) {
    ADDITION("+"),
    SUBTRACTION("-"),
    MULTIPLICATION("*"),
    DIVISION("/"),
    EQUALS("=");
}

class ChooseOperationActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var operationTextView: TextView
    private lateinit var acceptButton: Button
    private var currentOperation: MathOperation = MathOperation.ADDITION
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var vibrator: Vibrator
    private lateinit var clickSoundPlayer: MediaPlayer

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_operation)

        textToSpeech = TextToSpeech(this, this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        clickSoundPlayer = MediaPlayer.create(this, R.raw.click_sound)

        operationTextView = findViewById(R.id.operationTextView)
        acceptButton = findViewById(R.id.acceptButton)

        // Set initial operation
        updateOperationText(currentOperation)

        // Set a click listener for the root layout to change the operation on tap
        findViewById<View>(android.R.id.content).setOnClickListener {
            toggleOperation()
            vibrate(50)    // Vibrate for 50 milliseconds
            when (currentOperation) {
                MathOperation.ADDITION -> speak("Dodawanie.")
                MathOperation.SUBTRACTION -> speak("Odejmowanie.")
                MathOperation.MULTIPLICATION -> speak("Mnożenie.")
                MathOperation.DIVISION -> speak("Dzielenie.")
                else -> {
                    speak("Równa się.")
                }
            }
        }

        // Set a click listener for the Accept button
        acceptButton.setOnClickListener {
            textToSpeech.stop()
            vibrate(400)    // Vibrate for 400 milliseconds
            playClickSound()

            // Add the current operation symbol to the shared equation string
            addOperationToEquation()

            if (currentOperation == MathOperation.EQUALS) {
                // If the current operation is EQUALS, navigate to ResultActivity
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EQUATION_KEY, DataHolder.getInstance().currentEquation)
                startActivity(intent)
            } else {
                // Navigate to NumberInputActivity (or any other activity you want)
                val intent = Intent(this, NumberInputActivity::class.java)
                startActivity(intent)
            }

            Log.d("ChooseOperationActivity", "Current Equation: ${DataHolder.getInstance().currentEquation}")
        }

        onBackPressedDispatcher.addCallback(this) {
            showExitPrompt()
        }
    }

    override fun onDestroy() {
        // Shutdown the text-to-speech engine when the activity is destroyed
        textToSpeech.stop()
        textToSpeech.shutdown()
        clickSoundPlayer.release()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set the language for the text-to-speech engine
            val result = textToSpeech.setLanguage(Locale("pl", "PL"))

            // Check if the language is supported
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Log an error or show a message if the language is not supported
            } else {
                // Speak the text when the activity is created
                speak("Wybierz operację matematyczną. Dodawanie.")
            }
        } else {
            // Log an error if the text-to-speech engine initialization failed
        }
    }

    private fun speak(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
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

    private fun toggleOperation() {
        // Toggle between addition, subtraction, multiplication, and division
        currentOperation = when (currentOperation) {
            MathOperation.ADDITION -> MathOperation.SUBTRACTION
            MathOperation.SUBTRACTION -> MathOperation.MULTIPLICATION
            MathOperation.MULTIPLICATION -> MathOperation.DIVISION
            MathOperation.DIVISION -> MathOperation.EQUALS
            MathOperation.EQUALS -> MathOperation.ADDITION // Reset to ADDITION when EQUALS is pressed
        }

        // Update the operation text
        updateOperationText(currentOperation)
    }

    private fun updateOperationText(operation: MathOperation) {
        operationTextView.text = operation.symbol
    }

    private fun addOperationToEquation() {
        // Assuming DataHolder is a singleton class with a shared equation string
        DataHolder.getInstance().currentEquation += currentOperation.symbol
    }
}
