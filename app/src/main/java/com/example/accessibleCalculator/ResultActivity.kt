package com.example.accessibleCalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity

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

        resultTextView.text = "Result: $result"
    }

    private fun evaluateEquation(equation: String?): Int {
        if (equation.isNullOrEmpty()) {
            return 0
        }

        // Split the equation into parts based on operators
        val parts = equation.split(Regex("[-+*/=]"))

        // List to store the numbers extracted from the equation
        val numbers = mutableListOf<Int>()

        // Extract numbers from parts and add them to the list
        for (part in parts) {
            if (part.isNotEmpty()) {
                numbers.add(part.toInt())
            }
        }

        // List to store the operators
        val operators = equation.replace(Regex("[0-9]"), "")

        // Perform the calculation
        var result = numbers.firstOrNull() ?: 0
        for ((i, number) in numbers.drop(1).withIndex()) {
            when (operators[i]) {
                '+' -> result += number
                '-' -> result -= number
                '*' -> result *= number
                '/' -> result /= number
            }
        }

        return result
    }
}
