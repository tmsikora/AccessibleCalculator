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
import androidx.activity.addCallback
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class InstructionActivity : BaseActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val delayedTimeMillis: Long = 31500 // 31,5 seconds

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        // Show the instructions
        val instructionsTextView: TextView = findViewById(R.id.instructionsTextView)
        instructionsTextView.text = getInstructionsText()
        speak(getInstructionsText())

        // Set a delayed runnable to finish the activity after a certain time
        handler.postDelayed({
            navigateToNumberInput()
            finish()
        }, delayedTimeMillis)

        // Set a touch listener to finish the activity on touch
        findViewById<View>(android.R.id.content).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                textToSpeech.stop()
                vibrate(400)
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
        // Remove the delayed runnable callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
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
