package com.example.accessibleCalculator

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.annotation.RequiresApi

class InstructionActivity : ComponentActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val delayedTimeMillis: Long = 32000 // 32 seconds
    private lateinit var clickSoundPlayer: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        TextToSpeechManager.initialize(this, getInstructionsText())

        val vibratorManager = VibratorManager.getInstance(this)
        clickSoundPlayer = MediaPlayer.create(this, R.raw.click_sound)

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
                TextToSpeechManager.shutdown()
                vibratorManager.vibrate(400)
                playClickSound()
                navigateToNumberInput()
                finish()
            }
            true
        }

        onBackPressedDispatcher.addCallback(this) {
            showExitPrompt()
        }
    }


    override fun onDestroy() {
        // Stop and shutdown the text-to-speech engine when the activity is destroyed
        TextToSpeechManager.shutdown()
        // Remove the delayed runnable callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
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
