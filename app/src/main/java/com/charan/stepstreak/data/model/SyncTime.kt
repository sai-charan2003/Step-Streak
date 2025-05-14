package com.charan.stepstreak.data.model

enum class SyncTime(val minutes : Long){
    NEVER(-1),
    FIFTEEN_MINUTES(15),
    THIRTY_MINUTES(30),
    ONE_HOUR(60),
    TWO_HOURS(120),
    SIX_HOURS(360),
    TWELVE_HOURS(720),
    ONE_DAY(1440);

    fun getName() : String{
        return when(this){
            NEVER -> "Never"
            FIFTEEN_MINUTES -> "15 Minutes"
            THIRTY_MINUTES -> "30 Minutes"
            ONE_HOUR -> "1 Hour"
            TWO_HOURS -> "2 Hours"
            SIX_HOURS -> "6 Hours"
            TWELVE_HOURS -> "12 Hours"
            ONE_DAY -> "1 Day"
        }
    }
}
