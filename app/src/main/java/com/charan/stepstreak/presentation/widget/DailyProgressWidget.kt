package com.charan.stepstreak.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.BackgroundModifier.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.charan.stepstreak.MainActivity
import com.charan.stepstreak.R
import com.charan.stepstreak.data.repository.WidgetRepo
import com.charan.stepstreak.data.repository.impl.WidgetRepoImp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat

class DailyProgressWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {

        val repo = WidgetRepoImp.getInstance(context)
        provideContent {
            GlanceTheme {
                DailyProgressWidgetContent(
                    repo,
                    GlanceModifier.clickable(
                        actionStartActivity(
                            Intent(context, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    )
                )

            }
        }
    }

}
@AndroidEntryPoint
class DailyProgressWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = DailyProgressWidget()
}

@SuppressLint("RestrictedApi")
@Composable
fun DailyProgressWidgetContent(
    repo: WidgetRepo,
    modifier : GlanceModifier
) {
    var todayData by remember { mutableStateOf(WidgetState()) }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            repo.getStepData().collect {
                todayData = it
            }
        }
    }

    val stepsData = todayData.todayData
    val steps = stepsData?.steps ?: 0
    val targetSteps = stepsData?.targetSteps ?: 10000
    val percentage = (steps.toFloat() / targetSteps).coerceIn(0f, 1f)
    val formattedSteps = NumberFormat.getNumberInstance().format(steps)
    val formattedTarget = NumberFormat.getNumberInstance().format(targetSteps)


    val progress = (percentage * 100).toInt()
    val progressColor = when {
        percentage < 0.3f -> GlanceTheme.colors.error
        percentage < 0.7f -> GlanceTheme.colors.secondary
        else -> GlanceTheme.colors.primary
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .padding(12.dp)
            .cornerRadius(16.dp)
            .then(modifier),

    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.Top,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {

            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.applogo),
                    contentDescription = "Steps Icon",
                    modifier = GlanceModifier.size(20.dp)
                )

                Text(
                    text = "Today's Progress",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = androidx.glance.text.FontWeight.Medium
                    ),
                    modifier = GlanceModifier.padding(start = 8.dp)
                )

            }
            Spacer(modifier = GlanceModifier.defaultWeight())
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {


                Column(
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    modifier = GlanceModifier.padding(4.dp)
                ) {
                    Text(
                        text = formattedSteps,
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 31.sp,
                            fontWeight = androidx.glance.text.FontWeight.Bold
                        )
                    )
                    Text(
                        text = "steps",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    )
                }
            }
            Spacer(modifier = GlanceModifier.defaultWeight())


            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.Horizontal.Start
                ) {
                    Text(
                        text = "Streak",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = todayData.streak.toString(),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 19.sp,
                            fontWeight = androidx.glance.text.FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                Column(
                    horizontalAlignment = Alignment.Horizontal.End
                ) {
                    Text(
                        text = "Progress",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "$progress%",
                        style = TextStyle(
                            color = progressColor,
                            fontSize = 19.sp,
                            fontWeight = androidx.glance.text.FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}




