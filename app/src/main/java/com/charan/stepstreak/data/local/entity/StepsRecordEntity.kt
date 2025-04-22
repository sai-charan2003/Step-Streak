package com.charan.stepstreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps_record")
data class StepsRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long?= null,
    val steps: Int? = null,
    val stepTarget : Int? = null,
    val uuid : String? = null,
    val date : Long? = null
)
