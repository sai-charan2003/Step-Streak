package com.charan.stepstreak.presentation.common.components

import com.charan.stepstreak.data.model.StatType

sealed interface StatEvents {
    data class OnStatTypeSelected(val statType: StatType) : StatEvents
    object DecrementPeriod : StatEvents
    object IncrementPeriod : StatEvents
}