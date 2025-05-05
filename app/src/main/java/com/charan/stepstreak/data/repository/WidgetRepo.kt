package com.charan.stepstreak.data.repository

import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.presentation.widget.WidgetState
import kotlinx.coroutines.flow.Flow

interface WidgetRepo {

    fun getWeeklyStreak() : Flow<WidgetState>

    suspend fun updateWidget()


}