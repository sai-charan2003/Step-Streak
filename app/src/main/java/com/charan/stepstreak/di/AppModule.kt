package com.charan.stepstreak.di

import android.content.Context
import com.charan.stepstreak.data.local.AppDatabase
import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.repository.impl.StepsRecordRepoImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideStepsRecordDao(database: AppDatabase) : StepsRecordDao{
        return database.stepsRecordDao()
    }

    @Provides
    @Singleton
    fun provideStepsRecordRepo(stepsRecordDao: StepsRecordDao): StepsRecordRepo {
        return StepsRecordRepoImp(stepsRecordDao)

    }

}