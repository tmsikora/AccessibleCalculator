package com.example.accessibleCalculator.managers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi

@Suppress("DEPRECATION")
class VibratorManager private constructor(context: Context) {

    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    companion object {
        @Volatile
        private var instance: VibratorManager? = null

        fun getInstance(context: Context): VibratorManager {
            return instance ?: synchronized(this) {
                instance ?: VibratorManager(context).also { instance = it }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun vibrate(milliseconds: Long) {
        val vibrationEffect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }
}
