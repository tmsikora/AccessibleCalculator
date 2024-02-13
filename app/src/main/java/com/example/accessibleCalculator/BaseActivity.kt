package com.example.accessibleCalculator

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
open class BaseActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    lateinit var textToSpeech: TextToSpeech
    private var textToSpeak: String? = ""
    private var initialized = false

    private lateinit var vibrator: Vibrator
    private lateinit var clickSoundPlayer: MediaPlayer

    private var colorAnimation: ValueAnimator? = null
    private var isAnimationStarted: Boolean = false

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                pauseTextToSpeech()
            } else if (intent.action == Intent.ACTION_SCREEN_ON) {
                resumeTextToSpeech()
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textToSpeech = TextToSpeech(this, this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        clickSoundPlayer = MediaPlayer.create(this, R.raw.click_sound)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(screenReceiver, filter)
        resumeTextToSpeech()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(screenReceiver)
        pauseTextToSpeech()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
        clickSoundPlayer.release()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("pl", "PL"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle language not supported error
            }
            else
            {
                initialized = true
                textToSpeak?.let { speak(it) }
            }
        } else {
            // Handle initialization failure
        }
    }

    fun speak(text: String) {
        if (initialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            textToSpeak += text
        }
    }

    private fun pauseTextToSpeech() {
        if (::textToSpeech.isInitialized && textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
    }

    private fun resumeTextToSpeech() {
        if (!::textToSpeech.isInitialized) {
            textToSpeech = TextToSpeech(this, this)
        }
    }

    protected fun vibrate(milliseconds: Long) {
        val vibrationEffect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }

    protected fun playClickSound() {
        clickSoundPlayer.start()
    }

    protected fun showExitPrompt() {
        AlertDialog.Builder(this)
            .setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setPositiveButton("Tak") { _, _ -> finishAffinity() }
            .setNegativeButton("Nie") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    protected fun showBackToMainPrompt() {
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

    protected fun startColorAnimation() {
        val isDarkMode = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

        val colorFrom = if (isDarkMode) Color.parseColor("#121212") else Color.WHITE
        val colorTo = Color.parseColor("#009100")
        colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
            duration = if (isDarkMode) 2000 else 1300   // 2 seconds in dark mode; 1,3 seconds in light mode
            addUpdateListener { animator ->
                val color = animator.animatedValue as Int
                // Set the background color of the root layout
                findViewById<View>(android.R.id.content).setBackgroundColor(color)
            }
        }
        colorAnimation?.start()
        isAnimationStarted = true
    }

    protected fun reverseColorAnimation() {
        colorAnimation?.let { animation ->
            if (animation.isRunning || isAnimationStarted) {
                animation.reverse()
                isAnimationStarted = false
            }
        }
    }
}
