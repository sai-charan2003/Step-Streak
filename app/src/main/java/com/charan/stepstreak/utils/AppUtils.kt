package com.charan.stepstreak.utils

import android.annotation.SuppressLint
import androidx.glance.color.ColorProvider
import androidx.compose.ui.graphics.Color
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.FixedColorProvider

object AppUtils {
    @SuppressLint("RestrictedApi")
    fun getProgressColor(
        steps: Long,
        targetSteps: Long
    ): ColorProvider {
        if (targetSteps == 0L) return FixedColorProvider(Color(0xFFE0F2F1))

        val percentage = (steps.toFloat() / targetSteps)

        val lightGreen = Color(0xFFA5D6A7)
        val darkGreen = Color(0xFF2E7D32)


        val red = lerp(lightGreen.red, darkGreen.red, percentage)
        val green = lerp(lightGreen.green, darkGreen.green, percentage)
        val blue = lerp(lightGreen.blue, darkGreen.blue, percentage)

        return FixedColorProvider(Color(red, green, blue))
    }


    private fun lerp(start: Float, stop: Float, fraction: Float): Float {
        return start + (stop - start) * fraction
    }


}