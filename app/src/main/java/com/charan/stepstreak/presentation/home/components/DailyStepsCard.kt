package com.charan.stepstreak.presentation.home.components

import android.graphics.BlendMode
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.charan.stepstreak.presentation.home.StepsData
import com.charan.stepstreak.utils.toBarChar
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData

private val roundedRadius = 12.dp
private val squareRadius = 4.dp
private val verticalPadding = 8.dp

private val firstShape = RoundedCornerShape(
    topStart = roundedRadius,
    topEnd = roundedRadius,
    bottomStart = squareRadius,
    bottomEnd = squareRadius
)

private val lastShape = RoundedCornerShape(
    topStart = squareRadius,
    topEnd = squareRadius,
    bottomStart = roundedRadius,
    bottomEnd = roundedRadius
)

private val middleShape = RoundedCornerShape(size = squareRadius)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DailyStepsCard(
    stepsData: List<StepsData>
) {

    ElevatedCard (
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Steps",
                style = MaterialTheme.typography.titleMediumEmphasized.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(6.dp))

            stepsData.forEach {
                StepsCard(
                    totalSteps = it.steps,
                    formatDate = it.formattedDate,
                    targetSteps = it.targetSteps,
                    isFirst = stepsData.indexOf(it) == 0,
                    isLast = stepsData.indexOf(it) == stepsData.size - 1
                )
            }
        }
    }

}

@Preview
@Composable
fun DailyStepsCardPreview(){
    StepsCard(
        totalSteps = 10000,
        targetSteps = 10000,
        "",
        true,
        true
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StepsCard(
    totalSteps: Long,
    targetSteps: Long,
    formatDate: String,
    isFirst : Boolean,
    isLast : Boolean,
    isTargetReached : Boolean = totalSteps >= targetSteps,
    progress : Float = (totalSteps.toFloat() / targetSteps.toFloat())
) {
    val badgeColor = MaterialTheme.colorScheme.tertiary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = if (isFirst) {
            firstShape
        } else if (isLast) {
            lastShape
        } else {
            middleShape
        },

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .width(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = progress)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                )
            }


            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formatDate,
                    style = MaterialTheme.typography.titleSmallEmphasized.copy(fontWeight = FontWeight.Bold)
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically) {
                if (isTargetReached) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .padding(end = 15.dp)
                            .drawWithCache {
                                val pentagon = RoundedPolygon.star(
                                    numVerticesPerRadius = 6,
                                    radius = 60f,
                                    innerRadius = 30f,
                                    rounding = CornerRounding(50f),
                                    centerX = size.width / 2f,
                                    centerY = size.height / 2f,)
                                val path = pentagon.toPath().asComposePath()
                                onDrawBehind {
                                    drawPath(path, badgeColor)


                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.MilitaryTech,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }




                Text(
                    text = "%,d".format(totalSteps),
                    style = MaterialTheme.typography.titleLargeEmphasized.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                )
            }
        }
    }
}



