package com.charan.stepstreak.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.FixedColorProvider

object Utils {
    @SuppressLint("RestrictedApi")
    fun getProgressColor(
        steps: Long,
        targetSteps: Long
    ): ColorProvider {
        if (targetSteps == 0L) return FixedColorProvider(Color(0xFFE0F2F1))

        val percentage = (steps.toFloat() / targetSteps).coerceIn(0f, 1f)

        val lightGray = Color(0xFFEEEEEE)
        val green = Color(0xFF2E7D32)

        val red = lerp(lightGray.red, green.red, percentage)
        val greenChannel = lerp(lightGray.green, green.green, percentage)
        val blue = lerp(lightGray.blue, green.blue, percentage)

        return FixedColorProvider(Color(red, greenChannel, blue))
    }



    private fun lerp(start: Float, stop: Float, fraction: Float): Float {
        return start + (stop - start) * fraction
    }

    private val roundedRadius = 12.dp
    private val squareRadius = 4.dp

    val firstShape = RoundedCornerShape(
        topStart = roundedRadius,
        topEnd = roundedRadius,
        bottomStart = squareRadius,
        bottomEnd = squareRadius
    )

    val lastShape = RoundedCornerShape(
        topStart = squareRadius,
        topEnd = squareRadius,
        bottomStart = roundedRadius,
        bottomEnd = roundedRadius
    )

    val middleShape = RoundedCornerShape(size = squareRadius)


}