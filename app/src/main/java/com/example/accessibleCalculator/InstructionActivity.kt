package com.example.accessibleCalculator

import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.accessibleCalculator.managers.ClickSoundPlayerManager
import com.example.accessibleCalculator.managers.TextToSpeechManager
import com.example.accessibleCalculator.managers.VibratorManager
import com.example.accessibleCalculator.utils.ExitPrompt

class InstructionActivity : ComponentActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val delayedTimeMillis: Long = 32000 // 32 seconds

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        TextToSpeechManager.initialize(this, getInstructionsText())
        val vibratorManager = VibratorManager.getInstance(this)
        val clickSoundPlayerManager = ClickSoundPlayerManager.getInstance(this)

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
                clickSoundPlayerManager.playClickSound()
                navigateToNumberInput()
                finish()
            }
            true
        }

        onBackPressedDispatcher.addCallback(this) {
            ExitPrompt.showExitPromptSimple(this@InstructionActivity)
        }
    }

    override fun onDestroy() {
        // Stop and shutdown the text-to-speech engine when the activity is destroyed
        TextToSpeechManager.shutdown()
        // Remove the delayed runnable callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        ClickSoundPlayerManager.getInstance(this).release()
        super.onDestroy()
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
