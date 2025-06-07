package com.charan.stepstreak.data.model

enum class ThemeEnum {
    SYSTEM,
    LIGHT,
    DARK;

    fun getName(): String {
        return when (this) {
            SYSTEM -> "System Default"
            LIGHT -> "Light"
            DARK -> "Dark"
        }
    }
    companion object {
        fun fromName(name: String): ThemeEnum {
            return entries.firstOrNull { it.getName().equals(name, ignoreCase = true) }
                ?: SYSTEM
        }
    }
}