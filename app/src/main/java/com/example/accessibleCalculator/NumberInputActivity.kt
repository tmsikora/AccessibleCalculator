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
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class NumberInputActivity : BaseActivity() {

    private var currentNumber: Int = 0
    private lateinit var numberTextView: TextView
    private lateinit var acceptButton: Button
    private lateinit var gestureDetector: GestureDetector
    private var isLongPressing = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_input)

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                isLongPressing = true
                performActionOnAccept()
            }
        })

        numberTextView = findViewById(R.id.numberTextView)
        acceptButton = findViewById(R.id.acceptButton)

        if (DataHolder.currentEquation != "" && DataHolder.currentEquation.last() == '÷') {
            currentNumber = 1
        }
        // Set initial number
        updateNumberTextView()
        speak("Wybierz liczbę. $currentNumber")

        // Set a touch listener to increase the number based on the number of fingers pressed
        findViewById<View>(android.R.id.content).setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_POINTER_UP -> {
                    // Reset long press flag
                    isLongPressing = false
                    // Start long press detection
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isLongPressing) {
                            performActionOnAccept()
                        }
                    }, 4000) // 4 seconds

                    // Increment the current number by 1 for each finger press
                    currentNumber++
                    // Update the TextView to reflect the new number
                    updateNumberTextView()
                    // Vibrate for 50 milliseconds
                    vibrate(50)
                    // Speak the updated number
                    speak("$currentNumber")
                    // Cancel long press detection
                    //isLongPressing = false
                }
            }
            gestureDetector.onTouchEvent(event)
            true
        }

        // Set a click listener for the Accept button
        acceptButton.setOnClickListener {
            performActionOnAccept()
        }

        onBackPressedDispatcher.addCallback(this) {
            showBackToMainPrompt()
        }
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
