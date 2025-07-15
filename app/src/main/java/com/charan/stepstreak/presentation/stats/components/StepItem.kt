package com.charan.stepstreak.presentation.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.charan.stepstreak.utils.Utils.firstShape
import com.charan.stepstreak.utils.Utils.lastShape
import com.charan.stepstreak.utils.Utils.middleShape

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StepsCard(
    modifier: Modifier = Modifier,
    totalSteps: Long,
    targetSteps: Long,
    formatDate: String,
    isFirst : Boolean,
    isLast : Boolean,
    isTargetReached : Boolean = totalSteps >= targetSteps,
    progress : Float = (totalSteps.toFloat() / targetSteps.toFloat()),

    ) {
    val badgeColor = MaterialTheme.colorScheme.tertiary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .then(modifier),
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
                .padding(12.dp),
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