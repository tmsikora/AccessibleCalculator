package com.example.accessibleCalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class NumberInputActivity : BaseActivity() {

    private var currentNumber: Int = 0
    private lateinit var numberTextView: TextView
    private lateinit var gestureDetector: GestureDetector
    private var isLongPressing = false
    private val handler = Handler(Looper.getMainLooper())
    private val delayedTimeMillis: Long = 1000 // 1 second

    @SuppressLint("ClickableViewAccessibility", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_input)

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                isLongPressing = true
                handler.removeCallbacksAndMessages(null)
                // Start long press detection
                handler.postDelayed({
                    if (isLongPressing) {
                        performActionOnAccept()
                    }
                }, delayedTimeMillis)
            }
        })

        numberTextView = findViewById(R.id.numberTextView)

        if (DataHolder.currentEquation != "" && DataHolder.currentEquation.last() == '÷') {
            currentNumber = 1
        }
        // Set initial number
        updateNumberTextView()
        speak("Wybierz liczbę. $currentNumber")

        // Set a touch listener to increase the number based on the number of fingers pressed
        findViewById<View>(android.R.id.content).setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_POINTER_DOWN -> {
                    // Start the animation
                    startColorAnimation()
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_POINTER_UP -> {
                    reverseColorAnimation()
                    // Cancel long press detection
                    isLongPressing = false
                    // Increment the current number by 1 for each finger press
                    currentNumber++
                    // Update the TextView to reflect the new number
                    updateNumberTextView()
                    // Vibrate for 50 milliseconds
                    vibrate(50)
                    // Speak the updated number
                    speak("$currentNumber")
                }
            }
            gestureDetector.onTouchEvent(event)
            true
        }

        onBackPressedDispatcher.addCallback(this) {
            showBackToMainPrompt()
        }
    }

    override fun onDestroy() {
        // Remove the delayed runnable callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun performActionOnAccept() {
        isLongPressing = false
        textToSpeech.stop()
        vibrate(400)    // Vibrate for 400 milliseconds
        playClickSound()
        // Add the current number to the shared equation string
        addNumberToEquation()
        // Navigate to ChooseOperationActivity
        val intent = Intent(this, ChooseOperationActivity::class.java)
        startActivity(intent)
        Log.d("NumberInputActivity", "Current Equation: ${DataHolder.getInstance().currentEquation}")
    }

    private fun updateNumberTextView() {
        numberTextView.text = currentNumber.toString()
    }

    private fun addNumberToEquation() {
        // Assuming com.example.accessibleCalculator.DataHolder is a singleton class with a shared equation string
        DataHolder.getInstance().currentEquation += currentNumber.toString()
    }
}
