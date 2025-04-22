package com.charan.stepstreak.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.local.entity.StepsRecordEntity

@Database(entities = [StepsRecordEntity::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepsRecordDao(): StepsRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase?=null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                intArrayOf()
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "StepStreak"
                )
                    .fallbackToDestructiveMigrationFrom(false, 1).build()
                INSTANCE = instance
                instance
            }
        }
    }

}