package com.example.accessibleCalculator.managers

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

object TextToSpeechManager : TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private var initialized = false

    private var textToSpeak: String? = null

    fun initialize(context: Context, initialText: String) {
        if (!initialized) {
            textToSpeak = initialText
            textToSpeech = TextToSpeech(context, this)
        }
    }

    fun speak(text: String) {
        if (initialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            textToSpeak += text
        }
    }

    fun shutdown() {
            textToSpeech.stop()
            textToSpeech.shutdown()
            initialized = false
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
}
