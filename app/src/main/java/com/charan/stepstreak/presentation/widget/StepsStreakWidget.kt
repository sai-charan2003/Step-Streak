package com.charan.stepstreak.presentation.widget

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.service.autofill.Validators.or
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
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
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
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.charan.stepstreak.MainActivity
import com.charan.stepstreak.R
import com.charan.stepstreak.data.repository.WidgetRepo
import com.charan.stepstreak.data.repository.impl.WidgetRepoImp
import com.charan.stepstreak.data.worker.StepsUpdateWorker
import com.charan.stepstreak.presentation.components.ProgressTickMark
import com.charan.stepstreak.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)


    }
    override val glanceAppWidget: GlanceAppWidget
        get() = WeeklyStreakWidget()

}

@SuppressLint("RestrictedApi")
@Composable
fun WeeklyStreakWidgetContent(
    repo: WidgetRepo
) {
    var weeklyStreak by remember { mutableStateOf(WidgetState()) }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            repo.getWeeklyStreak().collect {
                weeklyStreak = it
            }
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
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = AndroidResourceImageProvider(R.drawable.streak),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp).padding(end = 6.dp)
            )
            Text(
                text = weeklyStreak.streak.toString(),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.primary
                )
            )
            Spacer(GlanceModifier.width(4.dp))
            Text(
                text = "Days",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = GlanceTheme.colors.onSurfaceVariant
                ),
                modifier = GlanceModifier.padding(top = 4.dp)
            )
        }
        Text(
            text = weeklyStreak.motiText,
            modifier = GlanceModifier.padding(bottom = 20.dp),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = GlanceTheme.colors.primary
            )

        )

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            weeklyStreak.stepsData.forEachIndexed { index, day ->
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.Start,
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
    steps: Long,
    targetSteps: Long,
    isCompleted: Boolean
) {
    val progressColor = AppUtils.getProgressColor(steps, targetSteps)

    Column(
        modifier = GlanceModifier
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .size(20.dp)
                .cornerRadius(100.dp)
                .background(progressColor),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Image(
                    provider = AndroidResourceImageProvider(R.drawable.rounded_check_24),
                    contentDescription = null,
                    modifier = GlanceModifier.size(12.dp),
                    colorFilter = ColorFilter.tint(colorProvider = ColorProvider(Color.White))
                )
            }
        }

        Spacer(GlanceModifier.height(4.dp))
        Text(
            text = weekName.uppercase(),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

