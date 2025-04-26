package com.charan.stepstreak.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.stepstreak.R

@Composable
fun StepsRecordItem(
    stepsRecord: StepsData,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            StepsProgress(
                steps = stepsRecord.steps,
                stepsTarger = stepsRecord.targetSteps,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = "${stepsRecord.steps} steps",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stepsRecord.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun StepsProgress(
    steps: Long,
    stepsTarger: Long,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator(
            progress = { (steps.toFloat() / stepsTarger.toFloat()).coerceIn(0f, 1f) },
            strokeWidth = 3.dp,
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.Center)
        )
        Image(
            painter = painterResource(R.drawable.rounded_steps_24),
            contentDescription = "Steps Icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
        )
    }
}

