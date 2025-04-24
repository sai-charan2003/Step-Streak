package com.charan.stepstreak.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "steps_record",
    indices = [Index(value = ["date"], unique = true)])
data class StepsRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long?= null,
    val steps: Long? = null,
    val stepTarget : Long? = null,
    val uuid : String? = null,
    val date : String? = null
)
