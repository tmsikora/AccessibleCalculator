package com.example.accessibleCalculator

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import com.example.accessibleCalculator.managers.ClickSoundPlayerManager
import com.example.accessibleCalculator.managers.TextToSpeechManager
import com.example.accessibleCalculator.managers.VibratorManager
import com.example.accessibleCalculator.utils.ExitPrompt

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TextToSpeechManager.initialize(this, "Aby rozpocząć obliczenia, dotknij ekranu.")
        val vibratorManager = VibratorManager.getInstance(this)
        val clickSoundPlayerManager = ClickSoundPlayerManager.getInstance(this)

        val rootLayout: View = this.findViewById(android.R.id.content)

        // Set a click listener on the root layout to start the InstructionActivity
        rootLayout.setOnClickListener {
            TextToSpeechManager.shutdown()
            vibratorManager.vibrate(400)
            clickSoundPlayerManager.playClickSound()
            val intent = Intent(this, NumberInputActivity::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this) {
            ExitPrompt.showExitPromptSimple(this@MainActivity)
        }

    }

    override fun onDestroy() {
        // Shutdown the text-to-speech engine when the activity is destroyed
        TextToSpeechManager.shutdown()
        ClickSoundPlayerManager.getInstance(this).release()
        super.onDestroy()
    }

}