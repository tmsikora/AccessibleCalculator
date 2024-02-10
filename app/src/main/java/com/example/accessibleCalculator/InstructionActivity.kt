package com.example.accessibleCalculator

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import java.util.Locale

class InstructionActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private val handler = Handler(Looper.getMainLooper())
    private val delayedTimeMillis: Long = 32000 // 32 seconds
    private lateinit var textToSpeech: TextToSpeech

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        textToSpeech = TextToSpeech(this, this)

        // Show the instructions
        val instructionsTextView: TextView = findViewById(R.id.instructionsTextView)
        instructionsTextView.text = getInstructionsText()

        // Set a delayed runnable to finish the activity after a certain time
        handler.postDelayed({
            navigateToNumberInput()
            finish()
        }, delayedTimeMillis)

        // Set a touch listener to finish the activity on touch
        findViewById<View>(android.R.id.content).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                textToSpeech.stop()
                navigateToNumberInput()
                finish()
            }
            true
        }

        onBackPressedDispatcher.addCallback(this) {
            showExitPrompt()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set the language for the text-to-speech engine
            val result = textToSpeech.setLanguage(Locale("pl", "PL"))

            // Check if the language is supported
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Log an error or show a message if the language is not supported
            } else {
                // Speak the instructions when the activity is created
                textToSpeech.speak(getInstructionsText(), TextToSpeech.QUEUE_FLUSH, null, null)
            }
        } else {
            // Log an error if the text-to-speech engine initialization failed
        }
    }

    override fun onDestroy() {
        // Stop and shutdown the text-to-speech engine when the activity is destroyed
        textToSpeech.stop()
        textToSpeech.shutdown()
        // Remove the delayed runnable callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun showExitPrompt() {
        // Show your custom prompt or dialog here
        // For example, you can use AlertDialog to display the prompt
        AlertDialog.Builder(this)
            .setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setPositiveButton("Tak") { _, _ -> finishAffinity() } // Exit the entire app
            .setNegativeButton("Nie") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getInstructionsText(): String {
        // Customize the instructions text here
        return "Instrukcja obsługi kalkulatora:\n\n" +
                "1. Wybierz liczbę. Każde dotknięcie ekranu zwiększa liczbę. Zatwierdź liczbę przytrzymując dłoń nisko nad ekranem.\n" +
                "2. Wybierz operację matematyczną. Przełączaj się między operacjami dotykając ekranu. Zatwierdź przytrzymując dłoń nisko nad ekranem.\n" +
                "3. Wybierz kolejną liczbę. Możesz wykonywać wiele obliczeń w jednym działaniu.\n" +
                "4. Aby uzyskać wynik, wybierz operację matematyczną \"równa się\".\n\n"
    }

    private fun navigateToNumberInput() {
        // Navigate to the NumberInputActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Remove any remaining callbacks to prevent unwanted navigation
        handler.removeCallbacksAndMessages(null)
    }
}
