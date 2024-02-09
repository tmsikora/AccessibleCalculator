package com.example.accessibleCalculator

import DataHolder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

enum class MathOperation(val symbol: String) {
    ADDITION("+"),
    SUBTRACTION("-"),
    MULTIPLICATION("*"),
    DIVISION("/"),
    EQUALS("=");
}

class ChooseOperationActivity : ComponentActivity() {

    private lateinit var operationTextView: TextView
    private lateinit var acceptButton: Button
    private var currentOperation: MathOperation = MathOperation.ADDITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_operation)

        operationTextView = findViewById(R.id.operationTextView)
        acceptButton = findViewById(R.id.acceptButton)

        // Set initial operation
        updateOperationText(currentOperation)

        // Set a click listener for the root layout to change the operation on tap
        findViewById<View>(android.R.id.content).setOnClickListener {
            toggleOperation()
        }

        // Set a click listener for the Accept button
        acceptButton.setOnClickListener {
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
        updateOperationText(currentOperation)
    }

    private fun updateOperationText(operation: MathOperation) {
        operationTextView.text = operation.symbol
    }

    private fun addOperationToEquation() {
        // Assuming DataHolder is a singleton class with a shared equation string
        DataHolder.getInstance().currentEquation += currentOperation.symbol
    }
}
