package com.charan.stepstreak.presentation.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.charan.stepstreak.presentation.common.PeriodStepsData
import kotlin.math.max

@Composable
fun StatGraph(
    periodStepData: PeriodStepsData,
    isSidePane: Boolean,
    targetStep: Long = 5000L,
    animationProgress:  Animatable<Float, AnimationVector1D> = remember { Animatable(0f) },
    barAnimations: List<Animatable<Float, AnimationVector1D>>
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    val teritiaryColor = MaterialTheme.colorScheme.tertiary
    val hapticFeedback = LocalHapticFeedback.current
    val highestValue = periodStepData.stepsData.maxOfOrNull { it.steps } ?: 0
    val maxValue = if (highestValue > targetStep) highestValue else targetStep
    val ySteps = 3
    val selectedBarIndex = remember { mutableStateOf<Int?>(null) }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSidePane) {
                    Modifier.fillMaxHeight()
                } else {
                    Modifier.height(250.dp)
                }
            )


    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val paddingLeft = 80f
        val paddingRight = 20f
        val paddingBottom = 50f
        val paddingTop = 20f
        val chartHeight = canvasHeight - paddingBottom - paddingTop
        val chartWidth = canvasWidth - paddingLeft - paddingRight
        val barWidth = chartWidth / (periodStepData.stepsData.size * 2)
        val space = barWidth

        val targetY = paddingTop + chartHeight - (targetStep / maxValue.toFloat()) * chartHeight
        val targetLineAlpha = animationProgress.value
        drawRoundRect(
            color = teritiaryColor.copy(alpha = targetLineAlpha),
            topLeft = Offset(paddingLeft, targetY - 1f),
            size = Size(chartWidth, 2f),
            cornerRadius = CornerRadius(80f, 80f)
        )

        if (targetLineAlpha > 0.5f) {
            val labelAlpha = ((targetLineAlpha - 0.5f) * 2f).coerceIn(0f, 1f)
            val animatedTargetPaint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                textSize = 28f
                color = Color(teritiaryColor.toArgb()).copy(alpha = labelAlpha).toArgb()
                textAlign = android.graphics.Paint.Align.RIGHT
            }
            drawContext.canvas.nativeCanvas.drawText(
                "%,d".format(targetStep),
                paddingLeft - 10f,
                targetY + 8f,
                animatedTargetPaint
            )
        }

        val yAxisLabels = mutableListOf(0L)
        val halfTarget = (targetStep / 2).coerceAtLeast(1)
        if (halfTarget !in yAxisLabels) yAxisLabels.add(halfTarget)
        if (targetStep !in yAxisLabels) yAxisLabels.add(targetStep)
        if (maxValue > targetStep && maxValue !in yAxisLabels) yAxisLabels.add(maxValue)

        val sortedLabels = yAxisLabels.sortedDescending()
        val minSpacingPx = 40f
        var lastYPosition = -Float.MAX_VALUE

        sortedLabels.forEach { yValue ->
            val fraction = yValue / maxValue.toFloat()
            val yPosition = paddingTop + chartHeight - (chartHeight * fraction)

            if (kotlin.math.abs(yPosition - lastYPosition) < minSpacingPx) return@forEach

            if (yValue != targetStep) {
                val animatedYLabelPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    textSize = 32f
                    color = Color(onSurfaceColor.toArgb()).copy(alpha = animationProgress.value).toArgb()
                    textAlign = android.graphics.Paint.Align.RIGHT
                }

                drawContext.canvas.nativeCanvas.drawText(
                    "%,d".format(yValue),
                    paddingLeft - 10f,
                    yPosition + 8f,
                    animatedYLabelPaint
                )
            }

            lastYPosition = yPosition
        }


        periodStepData.stepsData.forEachIndexed { index, data ->
            val barAnimationProgress = if (index < barAnimations.size) {
                barAnimations[index].value
            } else {
                0f
            }

            val fullBarHeight = (data.steps / maxValue.toFloat()) * chartHeight
            val animatedBarHeight = fullBarHeight * barAnimationProgress
            val x = paddingLeft+20 + index * (barWidth + space)
            val barColor = if (data.targetCompleted) Color(0xFF4CAF50) else primaryColor

            val scaledBarHeight = animatedBarHeight
            val scaledBarWidth = barWidth
            val offsetX = x +2 + (barWidth - scaledBarWidth) / 2
            val stepsPaint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                textSize = 32f
                color = Color(onSurfaceColor.toArgb()).copy(alpha = barAnimationProgress).toArgb()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            drawContext.canvas.nativeCanvas.drawText(
                "%,d".format(data.steps),
                x + barWidth / 2,
                paddingTop + chartHeight - animatedBarHeight - 20f,
                stepsPaint
            )

            drawRoundRect(
                color = barColor.copy(alpha = barAnimationProgress),
                topLeft = Offset(offsetX, paddingTop + chartHeight - scaledBarHeight),
                size = Size(scaledBarWidth, scaledBarHeight),
                cornerRadius = CornerRadius(80f, 80f)
            )

            if (selectedBarIndex.value == index && barAnimationProgress > 0.5f) {
                hapticFeedback.performHapticFeedback(LongPress)
                drawTooltip(
                    canvas = drawContext.canvas,
                    mainText = "${data.steps}",
                    dateText = data.formattedDate,
                    x = x + barWidth / 2,
                    y = paddingTop + chartHeight - animatedBarHeight - 20f,
                    alpha = 0.8f * barAnimationProgress,
                    primaryColor = onSurfaceColor
                )
            }

            if (barAnimationProgress > 0.3f) {
                val labelAlpha = ((barAnimationProgress - 0.3f) / 0.7f).coerceIn(0f, 1f)
                val animatedDayLabelPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    textSize = 28f
                    color = Color(onSurfaceColor.toArgb()).copy(alpha = labelAlpha).toArgb()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawContext.canvas.nativeCanvas.drawText(
                    data.day,
                    x + barWidth / 2,
                    canvasHeight - 10f,
                    animatedDayLabelPaint
                )
            }
        }
    }
}

private fun DrawScope.drawTooltip(
    canvas: Canvas,
    mainText: String,
    dateText: String,
    x: Float,
    y: Float,
    alpha: Float,
    primaryColor: Color
) {
    val mainPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 34f
        color = primaryColor.toArgb()
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    val datePaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 26f
        color = primaryColor.toArgb()
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT
    }

    val mainBounds = android.graphics.Rect()
    mainPaint.getTextBounds(mainText, 0, mainText.length, mainBounds)

    val dateBounds = android.graphics.Rect()
    datePaint.getTextBounds(dateText, 0, dateText.length, dateBounds)

    val padding = 20.dp.toPx()
    val spacing = 10.dp.toPx()

    val tooltipWidth = max(mainBounds.width(), dateBounds.width()) + 2 * padding

    val dateMetrics = datePaint.fontMetrics
    val mainMetrics = mainPaint.fontMetrics

    val dateHeight = dateMetrics.bottom - dateMetrics.top
    val mainHeight = mainMetrics.bottom - mainMetrics.top

    val tooltipHeight = dateHeight + spacing + mainHeight + 2 * padding

    val tooltipX = x - tooltipWidth / 2f
    val tooltipY = y - tooltipHeight - 8.dp.toPx()



    val tooltipCenterX = tooltipX + tooltipWidth / 2f
    val totalTextHeight = dateHeight + spacing + mainHeight
    val textTop = tooltipY + (tooltipHeight - totalTextHeight) / 2f

    val dateBaseline = textTop - dateMetrics.top
    val mainBaseline = (textTop + dateHeight + spacing) - mainMetrics.top


    datePaint.alpha = (255 * alpha).toInt()
    mainPaint.alpha = (255 * alpha).toInt()

    canvas.nativeCanvas.drawText(dateText, tooltipCenterX, dateBaseline, datePaint)
    canvas.nativeCanvas.drawText(mainText, tooltipCenterX, mainBaseline, mainPaint)
}