package com.charan.stepstreak.presentation.home.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charan.stepstreak.presentation.common.StepsData
import com.charan.stepstreak.presentation.common.PeriodStepsData
import com.charan.stepstreak.presentation.common.components.StatGraph
import com.charan.stepstreak.presentation.home.GraphData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SimpleBarChartWithAxes(
    modifier: Modifier = Modifier,
    weeklySteps: PeriodStepsData,
    targetStep: Long = 5000L,
    isSidePane : Boolean = false,
    graphData : List<GraphData>
) {

    ElevatedCard(
        modifier = modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Progress",
                        style = MaterialTheme.typography.titleMediumEmphasized.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = "Progress trend",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            if(weeklySteps.stepsData.isNotEmpty()) {
                StatGraph(
                    graphData = graphData,
                    isSidePane = isSidePane,
                    targetStep = targetStep,
                )
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun SimpleBarChartPreview() {
//    val sampleData = listOf(
//        StepsData(steps = 3000, day = "Mon", targetCompleted = false, formattedDate = "23rd may"),
//        StepsData(steps = 6000, day = "Tue", targetCompleted = false,formattedDate = "23rd may"),
//        StepsData(steps = 5000, day = "Wed", targetCompleted = true,formattedDate = "23rd may"),
//        StepsData(steps = 4000, day = "Thu", targetCompleted = false,formattedDate = "23rd may"),
//        StepsData(steps = 7500, day = "Fri", targetCompleted = false,formattedDate = "23rd may"),
//        StepsData(steps = 9000, day = "Sat", targetCompleted = true,formattedDate = "23rd may"),
//        StepsData(steps = 2000, day = "Sun", targetCompleted = false,formattedDate = "23rd may"),
//    )
//    val periodStepsData = PeriodStepsData(
//        averageSteps = sampleData.map { it.steps }.average().toLong(),
//        stepsData = sampleData
//    )
//
//    SimpleBarChartWithAxes(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(250.dp),
//
//    )
//}
