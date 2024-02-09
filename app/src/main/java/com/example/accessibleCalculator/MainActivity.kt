    package com.example.accessibleCalculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity

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
    }
}