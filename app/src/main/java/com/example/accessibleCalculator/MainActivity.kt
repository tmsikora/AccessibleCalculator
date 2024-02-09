package com.example.accessibleCalculator

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.addCallback

    class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootLayout: View = this.findViewById(android.R.id.content)

        // Set a click listener on the root layout to start the InstructionActivity
        rootLayout.setOnClickListener {
            val intent = Intent(this, NumberInputActivity::class.java)
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
            .setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setPositiveButton("Tak") { _, _ -> finishAffinity() } // Exit the entire app
            .setNegativeButton("Nie") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}