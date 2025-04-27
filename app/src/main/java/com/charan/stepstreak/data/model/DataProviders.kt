package com.charan.stepstreak.data.model

import android.graphics.drawable.Drawable

data class DataProviders(
    val name : String = "",
    val icon : Drawable,
    var isConnected : Boolean = false,
    val packageName : String = ""
)
