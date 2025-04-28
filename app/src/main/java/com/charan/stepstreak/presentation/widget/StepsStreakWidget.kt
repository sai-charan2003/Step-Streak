package com.charan.stepstreak.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.background
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.charan.stepstreak.R
import com.charan.stepstreak.data.repository.WidgetRepo
import com.charan.stepstreak.data.repository.impl.WidgetRepoImp
import com.charan.stepstreak.presentation.components.ProgressTickMark
import com.charan.stepstreak.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint

class WeeklyStreakWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val repo = WidgetRepoImp.getInstance(context)
        provideContent {
            GlanceTheme {
                WeeklyStreakWidgetContent(repo)

            }
        }
    }
}
@AndroidEntryPoint
class WeeklyStreakWidgetReceiver() : GlanceAppWidgetReceiver(){
    override val glanceAppWidget: GlanceAppWidget
        get() = WeeklyStreakWidget()

}

@Composable
fun WeeklyStreakWidgetContent(
    repo: WidgetRepo
) {
    var weeklyStreak by remember { mutableStateOf(WidgetState()) }

    LaunchedEffect(Unit) {
        repo.getWeeklyStreak().collect {
            weeklyStreak = it
        }
    }

    Column(
        modifier = GlanceModifier
            .padding(12.dp)
            .cornerRadius(12.dp)
            .background(GlanceTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "🔥 Weekly Streak",
            style = TextStyle(
                color = GlanceTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            modifier = GlanceModifier.padding(bottom = 20.dp)
        )

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            weeklyStreak.stepsData.forEachIndexed { index, day ->
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WeekStreakItem(
                        weekName = day.day.take(1),
                        isCompleted = day.targetCompleted,
                        steps = day.steps,
                        targetSteps = day.targetSteps
                    )
                }



            }
        }
    }
}

@SuppressLint("RestrictedApi")
@GlanceComposable
@Composable
fun WeekStreakItem(
    weekName: String,
    steps : Long,
    targetSteps : Long,
    isCompleted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = GlanceModifier
                .size(20.dp)
                .cornerRadius(100.dp)
                .background(AppUtils.getProgressColor(steps,targetSteps))
                ,

            contentAlignment = Alignment.Center
        ) {

        }

        Spacer(GlanceModifier.defaultWeight())

        Text(
            text = weekName,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
