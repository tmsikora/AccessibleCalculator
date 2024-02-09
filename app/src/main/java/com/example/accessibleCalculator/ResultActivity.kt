package com.example.accessibleCalculator

import DataHolder
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import java.util.Stack

class ResultActivity : ComponentActivity() {

    companion object {
        const val EQUATION_KEY = "equation"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val resultTextView: TextView = findViewById(R.id.resultTextView)

        // Retrieve the equation from the intent
        val equation = intent.getStringExtra(EQUATION_KEY)
        Log.d("ResultActivity", "Received equation from intent: $equation")

        // Parse and evaluate the equation
        val result = evaluateEquation(equation)
        Log.d("ResultActivity", "Calculated result: $result")

        // Format the result based on whether it's a whole number or not
        val formattedResult = if (result == result.toInt().toDouble()) {
            result.toInt().toString()  // If the result is a whole number, remove the decimal part
        } else {
            // If the result has a fractional part, format it to three decimal places
            val roundedResult = String.format("%.3f", result)
            if (roundedResult.length > result.toString().length) {
                // If the rounded result has more digits, use the original result
                result.toString()
            } else {
                // Otherwise, use the rounded result
                roundedResult
            }
        }

        resultTextView.text = "Wynik:\n$equation$formattedResult"

        // Find the root view of the layout
        val rootView = findViewById<View>(android.R.id.content)

        // Set a click listener on the root view
        rootView.setOnClickListener {
            // Clear the equation
            DataHolder.getInstance().currentEquation = ""
            // Start MainActivity when the screen is clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this) {
            showExitPrompt()
        }
    }

    private fun showExitPrompt() {
        // Show your custom prompt or dialog here
        // For example, you can use AlertDialog to display the prompt
        AlertDialog.Builder(this)
            .setMessage("Czy chcesz wrócić do ekranu głównego aplikacji?")
            .setPositiveButton("Tak") { _, _ ->
                DataHolder.getInstance().currentEquation = ""
                // Navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            .setNegativeButton("Nie") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun evaluateEquation(equation: String?): Double {
        if (equation.isNullOrEmpty()) {
            return 0.0
        }

        // Remove any spaces from the equation
        val cleanEquation = equation.replace("\\s".toRegex(), "")

        // Use a stack to evaluate the expression with proper precedence
        val numberStack = Stack<Double>()
        val operatorStack = Stack<Char>()

        // Operator precedence map
        val precedence = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2)

        var i = 0
        while (i < cleanEquation.length) {
            val c = cleanEquation[i]

            if (c.isDigit()) {
                // If the character is a digit, extract the whole number
                val stringBuilder = StringBuilder()
                while (i < cleanEquation.length && cleanEquation[i].isDigit()) {
                    stringBuilder.append(cleanEquation[i])
                    i++
                }
                numberStack.push(stringBuilder.toString().toDouble())
                continue
            } else if (c in "+-*/") {
                // If the character is an operator
                while (!operatorStack.isEmpty() && precedence.getOrDefault(c, 0) <= precedence.getOrDefault(operatorStack.peek(), 0)) {
                    performOperation(numberStack, operatorStack)
                }
                operatorStack.push(c)
            }
            i++
        }

        // Perform any remaining operations
        while (!operatorStack.isEmpty()) {
            performOperation(numberStack, operatorStack)
        }

        return numberStack.pop()
    }

    private fun performOperation(numberStack: Stack<Double>, operatorStack: Stack<Char>) {
        val operator = operatorStack.pop()
        val operand2 = numberStack.pop()
        val operand1 = numberStack.pop()
        val result = when (operator) {
            '+' -> operand1 + operand2
            '-' -> operand1 - operand2
            '*' -> operand1 * operand2
            '/' -> operand1 / operand2
            else -> throw IllegalArgumentException("Invalid operator: $operator")
        }
        numberStack.push(result)
    }
}
