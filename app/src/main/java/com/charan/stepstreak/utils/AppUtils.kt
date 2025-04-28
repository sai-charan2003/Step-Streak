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
        if (targetSteps == 0L) return FixedColorProvider(Color(0xFFE0F2F1)) // very light green

        val percentage = steps.toFloat() / targetSteps

        return when {
            percentage >= 1f -> FixedColorProvider(Color(0xFF006400)) // dark green
            percentage >= 0.75f -> FixedColorProvider(Color(0xFF2E7D32)) // medium-dark green
            percentage >= 0.5f -> FixedColorProvider(Color(0xFF66BB6A)) // medium green
            percentage >= 0.25f -> FixedColorProvider(Color(0xFFA5D6A7)) // light green
            else -> FixedColorProvider(Color(0xFFE0F2F1)) // very light green
        }
    }

}