package com.charan.stepstreak.presentation.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.units.percent
import com.charan.stepstreak.data.model.StatType
import com.charan.stepstreak.presentation.common.PeriodStepsData
import com.charan.stepstreak.presentation.common.StepsData

@Composable
fun StatGraph(
    periodStepData: PeriodStepsData,
    isSidePane: Boolean,
    targetStep: Long = 5000L,
    animationProgress: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) },
    barAnimations: List<Animatable<Float, AnimationVector1D>>,
    statType: StatType = StatType.WEEKLY
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val maxHeight = 200.dp
    val stepsList = periodStepData.stepsData

    val highestSteps = stepsList.maxOfOrNull { it.steps } ?: 0
    val maxSteps = targetStep.toFloat()
    val yLabels = listOf(
        targetStep.toInt(),
        (targetStep / 2).toInt(),
        0
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxHeight)
            .padding(horizontal = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .height(maxHeight)
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            yLabels.forEach { value ->
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxHeight),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                stepsList.forEachIndexed { index, stepData ->
                    Bar(
                        value = stepData.steps.toFloat(),
                        maxValue = maxSteps,
                        color = onSurfaceColor,
                        maxHeight = maxHeight,
                        isTargetReached = stepData.steps >= targetStep.toInt()
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                stepsList.forEach {
                    Text(
                        text = it.day,
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun Bar(
    value: Float,
    maxValue: Float,
    color: Color,
    maxHeight: Dp,
    isTargetReached : Boolean
) {
    val barHeightRatio = if (maxValue == 0f) 0f else value / maxValue
    Box(
        modifier = Modifier
            .width(20.dp)
            .fillMaxHeight(fraction = barHeightRatio)
            .clip(RoundedCornerShape(100.dp))
            .background(
                if(isTargetReached){
                    Color(0xFF4CAF50)
                } else{
                    MaterialTheme.colorScheme.primary
                }
            )
    )

}

@Preview
@Composable
fun StatGraphPreview() {
    val sampleSteps = listOf(
        StepsData(day = "Mon", steps = 1000),
        StepsData(day = "Tue", steps = 2000),
        StepsData(day = "Wed", steps = 3000),
        StepsData(day = "Thu", steps = 4000),
        StepsData(day = "Fri", steps = 6000),
        StepsData(day = "Sat", steps = 2500),
        StepsData(day = "Sun", steps = 1000)
    )
    val periodStepsData = PeriodStepsData(stepsData = sampleSteps)

    StatGraph(
        periodStepData = periodStepsData,
        isSidePane = false,
        targetStep = 5000L,
        animationProgress = remember { Animatable(0f) },
        barAnimations = List(sampleSteps.size) { Animatable(0f) },
    )
}