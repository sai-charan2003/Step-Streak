package com.charan.stepstreak.presentation.home.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charan.stepstreak.presentation.common.StepsData

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SimpleBarChartWithAxes(
    modifier: Modifier = Modifier,
    weeklySteps: List<StepsData>,
    targetStep: Long = 6000L
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val primaryColor = MaterialTheme.colorScheme.primary
    val teritiaryColor = MaterialTheme.colorScheme.tertiary
    val hapticFeedback = LocalHapticFeedback.current
    val highestValue = weeklySteps.maxOfOrNull { it.steps } ?: 0
    val maxValue = if (highestValue > targetStep) highestValue else targetStep
    val ySteps = 5
    val selectedBarIndex = remember { mutableStateOf<Int?>(null) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Progress",
                style = MaterialTheme.typography.titleMediumEmphasized.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val canvasWidth = size.width
                                val paddingLeft = 90f
                                val chartWidth = canvasWidth - paddingLeft
                                val barWidth = chartWidth / (weeklySteps.size * 2)
                                val space = barWidth
                                val barStartX = paddingLeft
                                val fullBarWidth = barWidth + space

                                event.changes.forEach { change ->
                                    val xOffset = change.position.x - barStartX
                                    if (xOffset >= 0) {
                                        val index = (xOffset / fullBarWidth).toInt()
                                        if (index in weeklySteps.indices) {
                                            selectedBarIndex.value = index
                                        }
                                    }

                                    if (change.pressed) {
                                        change.consume()
                                    }
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val paddingLeft = 90f
                val paddingBottom = 40f
                val chartHeight = canvasHeight - paddingBottom
                val chartWidth = canvasWidth - paddingLeft
                val barWidth = chartWidth / (weeklySteps.size * 2)
                val space = barWidth
                val targetY = chartHeight - (targetStep / maxValue.toFloat()) * chartHeight
                val yLabelPaint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    textSize = 24f
                    color = onSurfaceColor
                }
                drawRoundRect(
                    color = teritiaryColor,
                    topLeft = Offset(paddingLeft, targetY - 1f),
                    size = Size(canvasWidth - paddingLeft, 2f),
                    cornerRadius = CornerRadius(80f, 80f)
                )

                repeat(ySteps + 1) { i ->
                    val fraction = i / ySteps.toFloat()
                    val yValue = (maxValue * fraction).toInt()
                    val yPosition = chartHeight - (chartHeight * fraction)


                    drawContext.canvas.nativeCanvas.drawText(
                        yValue.toString(),
                        0f,
                        yPosition,
                        yLabelPaint
                    )
                }


                weeklySteps.forEachIndexed { index, data ->
                    val barHeight = (data.steps / maxValue.toFloat()) * chartHeight
                    val x = paddingLeft + index * (barWidth + space)
                    val barColor = if (data.targetCompleted) Color(0xFF4CAF50) else primaryColor

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, chartHeight - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(80f, 80f)
                    )

                    if (selectedBarIndex.value == index) {
                        hapticFeedback.performHapticFeedback(
                            LongPress
                        )
                        val paint = Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            textSize = 32f
                            color = onSurfaceColor
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            data.steps.toString(),
                            x + barWidth / 2,
                            chartHeight - barHeight - 10f,
                            paint
                        )
                    }


                    drawContext.canvas.nativeCanvas.drawText(
                        data.day,
                        x,
                        canvasHeight,
                        yLabelPaint
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SimpleBarChartPreview() {
    val sampleData = listOf(
        StepsData(steps = 3000, day = "Mon", targetCompleted = false),
        StepsData(steps = 6000, day = "Tue", targetCompleted = false),
        StepsData(steps = 10000, day = "Wed", targetCompleted = true),
        StepsData(steps = 4000, day = "Thu", targetCompleted = false),
        StepsData(steps = 7500, day = "Fri", targetCompleted = false),
        StepsData(steps = 9000, day = "Sat", targetCompleted = true),
        StepsData(steps = 2000, day = "Sun", targetCompleted = false),
    )

    SimpleBarChartWithAxes(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        weeklySteps = sampleData
    )
}
