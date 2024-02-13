package com.example.accessibleCalculator

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speak("Aby rozpocząć obliczenia, dotknij ekranu.")

        val rootLayout: View = findViewById(android.R.id.content)

        rootLayout.setOnClickListener {
            // Stop the text-to-speech, vibrate, play click sound, and start NumberInputActivity
            performActionOnClick()
        }

        // Handle back button press to show exit prompt
        onBackPressedDispatcher.addCallback(this) {
            showExitPrompt()
        }
    }

    private fun performActionOnClick() {
        textToSpeech.stop()
        vibrate(400)
        playClickSound()
        val intent = Intent(this, NumberInputActivity::class.java)
        startActivity(intent)
    }
}
