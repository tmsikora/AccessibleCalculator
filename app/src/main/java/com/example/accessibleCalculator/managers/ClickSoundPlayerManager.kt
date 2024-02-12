package com.example.accessibleCalculator.managers

import android.content.Context
import android.media.MediaPlayer
import com.example.accessibleCalculator.R

class ClickSoundPlayerManager private constructor(context: Context) {

    private var clickSoundPlayer: MediaPlayer? = null
    private var applicationContext = context

    companion object {
        @Volatile
        private var instance: ClickSoundPlayerManager? = null

        fun getInstance(context: Context): ClickSoundPlayerManager {
            return instance ?: synchronized(this) {
                instance ?: ClickSoundPlayerManager(context).also { instance = it }
            }
        }
    }

    fun playClickSound() {
        if (clickSoundPlayer == null) {
            clickSoundPlayer = MediaPlayer.create(applicationContext, R.raw.click_sound)
        }
        clickSoundPlayer?.start()
    }

    fun release() {
        clickSoundPlayer?.release()
        clickSoundPlayer = null
    }
}
