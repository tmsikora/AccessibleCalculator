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
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.RequiresApi

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class ChooseOperationActivity : BaseActivity(), SensorEventListener {

    private lateinit var operationTextView: TextView
    private var currentOperation: MathOperation = MathOperation.ADDITION
    private lateinit var gestureDetector: GestureDetector
    private var isLongPressing = false
    private val handler = Handler(Looper.getMainLooper())
    private val delayedTimeMillis: Long = 1000 // 1 second
    private var isAnimating = false

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private val handlerProximity = Handler()

    @SuppressLint("ClickableViewAccessibility", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_operation)

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
                    if (isLongPressing) {
                        performActionOnAccept()
                    }
                }, delayedTimeMillis)
            }
        })

        operationTextView = findViewById(R.id.operationTextView)

        // Set initial operation
        if (DataHolder.currentEquation.containsAny(
                MathOperation.ADDITION.symbol,
                MathOperation.SUBTRACTION.symbol,
                MathOperation.MULTIPLICATION.symbol,
                MathOperation.DIVISION.symbol)
        ) {
            currentOperation = MathOperation.EQUALS
        }
        updateOperationTextView(currentOperation)
        speak("Wybierz operację matematyczną.")
        speakOperationName()

        // Set a click listener for the root layout to change the operation on tap
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
                    toggleOperation()
                    vibrate(50)    // Vibrate for 50 milliseconds
                    speakOperationName()
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

        // Add the current operation symbol to the shared equation string
        addOperationToEquation()

        if (currentOperation == MathOperation.EQUALS) {
            // If the current operation is EQUALS, navigate to ResultActivity
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EQUATION_KEY, DataHolder.getInstance().currentEquation)
            startActivity(intent)
        } else {
            // Navigate to NumberInputActivity (or any other activity you want)
            val intent = Intent(this, NumberInputActivity::class.java)
            startActivity(intent)
        }

        Log.d("ChooseOperationActivity", "Current Equation: ${DataHolder.getInstance().currentEquation}")
    }

    private fun speakOperationName() {
        when (currentOperation) {
            MathOperation.ADDITION -> speak("Dodawanie.")
            MathOperation.SUBTRACTION -> speak("Odejmowanie.")
            MathOperation.MULTIPLICATION -> speak("Mnożenie.")
            MathOperation.DIVISION -> speak("Dzielenie.")
            else -> {
                speak("Równa się.")
            }
        }
    }

    private fun toggleOperation() {
        // Toggle between addition, subtraction, multiplication, and division
        currentOperation = when (currentOperation) {
            MathOperation.ADDITION -> MathOperation.SUBTRACTION
            MathOperation.SUBTRACTION -> MathOperation.MULTIPLICATION
            MathOperation.MULTIPLICATION -> MathOperation.DIVISION
            MathOperation.DIVISION -> MathOperation.EQUALS
            MathOperation.EQUALS -> MathOperation.ADDITION // Reset to ADDITION when EQUALS is pressed
        }

        // Update the operation text
        updateOperationTextView(currentOperation)
    }

    private fun String.containsAny(vararg chars: String): Boolean {
        for (char in chars) {
            if (this.contains(char)) {
                return true
            }
        }
        return false
    }

    private fun updateOperationTextView(operation: MathOperation) {
        operationTextView.text = operation.symbol
    }

    private fun addOperationToEquation() {
        // Assuming com.example.accessibleCalculator.DataHolder is a singleton class with a shared equation string
        DataHolder.getInstance().currentEquation += currentOperation.symbol
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
                }, 3000) // 3000 milliseconds = 3 seconds
            }
            else
            {
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
