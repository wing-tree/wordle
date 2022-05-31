package com.wing.tree.android.wordle.presentation.util

import android.content.Context
import android.os.*
import android.os.Vibrator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Vibrator @Inject constructor(@ApplicationContext context: Context) {
    private val vibrator by lazy {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val vibratorManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        } else {
            null
        }
    }

    private val vibrationEffect by lazy {
        val milliseconds = 20L
        val amplitude = 40

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.createOneShot(milliseconds, amplitude)
        } else {
            null
        }
    }

    fun vibrate() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val combinedVibration = vibrationEffect?.let {
                    CombinedVibration.createParallel(it)
                }

                combinedVibration?.let {  vibratorManager?.vibrate(it) }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> vibrator?.vibrate(vibrationEffect)
            else -> {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40L)
            }
        }
    }
}