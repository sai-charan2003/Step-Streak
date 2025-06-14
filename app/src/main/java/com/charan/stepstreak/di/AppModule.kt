package com.charan.stepstreak.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import com.charan.stepstreak.data.local.AppDatabase
import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.local.dao.UserSettingsDao
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.repository.UsersSettingsRepo
import com.charan.stepstreak.data.repository.WidgetRepo
import com.charan.stepstreak.data.repository.impl.DataStoreRepoImp
import com.charan.stepstreak.data.repository.impl.HealthConnectRepoImpl
import com.charan.stepstreak.data.repository.impl.StepsRecordRepoImp
import com.charan.stepstreak.data.repository.impl.UserSettingsRepoImp
import com.charan.stepstreak.data.repository.impl.WidgetRepoImp
import com.charan.stepstreak.utils.NotificationHelper
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
    fun provideSettingsDao(database: AppDatabase) : UserSettingsDao{
        return database.userSettingsDao()
    }

    @Provides
    @Singleton
    fun provideStepsRecordRepo(stepsRecordDao: StepsRecordDao,usersSettingsRepo: UsersSettingsRepo,dataStoreRepo: DataStoreRepo): StepsRecordRepo {
        return StepsRecordRepoImp(stepsRecordDao,usersSettingsRepo, dataStoreRepo)

    }
    @Provides
    @Singleton
    fun provideUserSettingsRepo(settingsDao: UserSettingsDao): UsersSettingsRepo {
        return UserSettingsRepoImp(settingsDao)

    }

    @Provides
    @Singleton
    fun provideHealthConnectClient(@ApplicationContext context: Context): HealthConnectClient {
        return HealthConnectClient.getOrCreate(context)

    }

    @Provides
    @Singleton
    fun provideHealthConnectRepo(healthConnectClient: HealthConnectClient,stepsRecordDao: StepsRecordDao,@ApplicationContext context: Context,dataStoreRepo: DataStoreRepo,usersSettingsRepo: UsersSettingsRepo): HealthConnectRepo {
        return HealthConnectRepoImpl(healthConnectClient,stepsRecordDao,context,dataStoreRepo,usersSettingsRepo)

    }

    @Provides
    @Singleton
    fun provideDataStoreRepo(@ApplicationContext context : Context) : DataStoreRepo {
        return DataStoreRepoImp(context)

    }

    @Provides
    @Singleton
    fun provideWidgetRepo(@ApplicationContext context : Context,healthConnectRepo: HealthConnectRepo,stepsRecordRepo: StepsRecordRepo,dataStoreRepo: DataStoreRepo) : WidgetRepo{
        return WidgetRepoImp(healthConnectRepo,stepsRecordRepo,dataStoreRepo,context)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }

}