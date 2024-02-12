package com.example.accessibleCalculator

import TextToSpeechManager
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.annotation.RequiresApi

class MainActivity : ComponentActivity() {

    private lateinit var clickSoundPlayer: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TextToSpeechManager.initialize(this, "Aby rozpocząć obliczenia, dotknij ekranu.")

        val vibratorManager = VibratorManager.getInstance(this)
        clickSoundPlayer = MediaPlayer.create(this, R.raw.click_sound)

        val rootLayout: View = this.findViewById(android.R.id.content)

        // Set a click listener on the root layout to start the InstructionActivity
        rootLayout.setOnClickListener {
            TextToSpeechManager.shutdown()
            vibratorManager.vibrate(400)
            playClickSound()
            val intent = Intent(this, NumberInputActivity::class.java)
            startActivity(intent)
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

    private fun playClickSound() {
        clickSoundPlayer.start()
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