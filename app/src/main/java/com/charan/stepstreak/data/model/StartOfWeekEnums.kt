package com.charan.stepstreak.data.model

enum class StartOfWeekEnums {
    SUNDAY,
    MONDAY;
    companion object {
        fun fromName(name: String): StartOfWeekEnums {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: MONDAY
        }

    }
    fun getName(): String {
        return when (this) {
            SUNDAY -> "Sunday"
            MONDAY -> "Monday"
        }
    }
}