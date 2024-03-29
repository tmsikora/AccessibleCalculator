package com.example.accessibleCalculator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import java.util.Stack

@RequiresApi(Build.VERSION_CODES.O)
class ResultActivity : BaseActivity() {

    private var formattedResult: String = ""

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

        // Parse and evaluate the equation
        val result = evaluateEquation(equation)

        // Format the result based on whether it's a whole number or not
        formattedResult = if (result == result.toInt().toDouble()) {
            result.toInt().toString()  // If the result is a whole number, remove the decimal part
        } else {
            // If the result has a fractional part, format it to three decimal places
            val roundedResult = String.format("%.3f", result)
            if (roundedResult.length > result.toString().length) {
                // If the rounded result has more digits, use the original result
                result.toString().replace(".", ",")
            } else {
                // Otherwise, use the rounded result
                roundedResult.replace(".", ",")
            }
        }

        if (equation != null) {
            if ((equation.length + formattedResult.length) > 12) {
                resultTextView.text = "$equation\n$formattedResult"
            } else {
                resultTextView.text = "$equation$formattedResult"
            }
        }

        speak("Wynik wynosi: $formattedResult")

        // Find the root view of the layout
        val rootView = findViewById<View>(android.R.id.content)

        rootView.setOnClickListener {
            textToSpeech.stop()
            vibrate(400)
            playClickSound()
            // Clear the equation
            DataHolder.getInstance().currentEquation = ""
            // Start MainActivity when the screen is clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this) {
            showBackToMainPrompt()
        }
    }

    private fun evaluateEquation(equation: String?): Double {
        if (equation.isNullOrEmpty()) {
            return 0.0
        }

        // Remove any spaces from the equation
        val cleanEquation = equation
            .replace("\\s".toRegex(), "")
            .replace(MathOperation.MULTIPLICATION.symbol, "*")
            .replace(MathOperation.DIVISION.symbol, "/")

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
            }
            else if (c in "+-*/") {
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
