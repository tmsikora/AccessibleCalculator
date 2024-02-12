package com.example.accessibleCalculator.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.example.accessibleCalculator.MainActivity

class ExitPrompt {

    companion object {
        fun showExitPrompt(context: Context) {
            // Show your custom prompt or dialog here
            // For example, you can use AlertDialog to display the prompt
            AlertDialog.Builder(context)
                .setMessage("Czy chcesz wrócić do ekranu głównego aplikacji?")
                .setPositiveButton("Tak") { _, _ ->
                    DataHolder.getInstance().currentEquation = ""
                    // Navigate to MainActivity
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    context.startActivity(intent)
                }
                .setNegativeButton("Nie") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        fun showExitPromptSimple(context: Context) {
            // Show your custom prompt or dialog here
            // For example, you can use AlertDialog to display the prompt
            AlertDialog.Builder(context)
                .setMessage("Czy na pewno chcesz wyjść z aplikacji?")
                .setPositiveButton("Tak") { dialog, _ ->
                    finishActivity(context)
                }
                .setNegativeButton("Nie") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        private fun finishActivity(context: Context) {
            // Finish the activity and exit the app
            if (context is Activity) {
                context.finishAffinity()
            }
        }
    }
}