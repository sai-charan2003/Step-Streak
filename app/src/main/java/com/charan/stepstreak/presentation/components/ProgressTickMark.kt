package com.charan.stepstreak.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.glance.background
import androidx.glance.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import com.charan.stepstreak.R

@SuppressLint("RestrictedApi")
@Composable
fun ProgressTickMark(
    isAchieved : Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = GlanceModifier
            .size(24.dp)
            .cornerRadius(100.dp)
            .background(Color.Green.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        if(isAchieved) {
            Image(
                provider = AndroidResourceImageProvider(R.drawable.rounded_check_24),
                null,
            )
        }


    }

}