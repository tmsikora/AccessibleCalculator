package com.example.accessibleCalculator

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textToSpeech = TextToSpeech(this, this)

        val rootLayout: View = this.findViewById(android.R.id.content)

        // Set a click listener on the root layout to start the InstructionActivity
        rootLayout.setOnClickListener {
            textToSpeech.stop()
            val intent = Intent(this, NumberInputActivity::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this) {
            showExitPrompt()
        }
    }

    override fun onDestroy() {
        // Shutdown the text-to-speech engine when the activity is destroyed
        textToSpeech.stop()
        textToSpeech.shutdown()
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
                speak("Aby rozpocząć obliczenia, dotknij ekranu.")
            }
        } else {
            // Log an error if the text-to-speech engine initialization failed
        }
    }

    private fun speak(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
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
}