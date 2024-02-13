package com.example.accessibleCalculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.RequiresApi

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class NumberInputActivity : BaseActivity(), SensorEventListener {

    private var currentNumber: Int = 0
    private lateinit var numberTextView: TextView
    private lateinit var gestureDetector: GestureDetector
    private var isLongPressing = false
    private val handler = Handler(Looper.getMainLooper())
    private val delayedTimeMillis: Long = 1000 // 1 second
    private var isAnimating: Boolean = false
    private var pointersOnScreen: Int = 0
    private var moreThanOneClick: Boolean = false

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private val handlerProximity = Handler()

    @SuppressLint("ClickableViewAccessibility", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_input)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                isLongPressing = true
                handler.removeCallbacksAndMessages(null)
                handlerProximity.removeCallbacksAndMessages(null)
                // Start long press detection
                handler.postDelayed({
                    if (!moreThanOneClick && isLongPressing) {
                        reverseColorAnimation()
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
                    pointersOnScreen++
                    if (pointersOnScreen == 1)
                    {
                        // Start the animation
                        startColorAnimation()
                    }
                    if (pointersOnScreen > 1)
                    {
                        // Reverse the animation
                        reverseColorAnimation()
                        moreThanOneClick = true
                    }
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_POINTER_UP -> {
                    pointersOnScreen--
                    if (pointersOnScreen == 0)
                    {
                        moreThanOneClick = false
                        // Reverse the animation
                        reverseColorAnimation()
                    }
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
        handlerProximity.removeCallbacksAndMessages(null)
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
    }

    private fun updateNumberTextView() {
        numberTextView.text = currentNumber.toString()
    }

    private fun addNumberToEquation() {
        DataHolder.getInstance().currentEquation += currentNumber.toString()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            if (distance < 5.0f) {
                if (!isAnimating) {
                    startColorAnimation()
                    isAnimating = true
                }
                handlerProximity.postDelayed({
                    performActionOnAccept()
                }, 3000) // 3 seconds
            }
            else {
                if (isAnimating) {
                    reverseColorAnimation()
                    isAnimating = false
                }
                handlerProximity.removeCallbacksAndMessages(null)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onResume() {
        // Register a listener for the sensor.
        super.onResume()
        proximitySensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
