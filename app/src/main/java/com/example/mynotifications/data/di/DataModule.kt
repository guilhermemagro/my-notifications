package com.example.mynotifications.data.di

import android.content.Context
import com.example.mynotifications.data.database.AppDatabase
import com.example.mynotifications.data.database.NotificationDao
import com.example.mynotifications.data.repository.NotificationRepository
import com.example.mynotifications.data.repository.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module(includes = [DataModule.Declarations::class])
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface Declarations {
        @Binds
        fun bindNotificationRepository(
            notificationRepositoryImpl: NotificationRepositoryImpl
        ): NotificationRepository
    }
}
