package com.islamic.prayertimesapp.di


import android.app.Application
import com.islamic.prayertimesapp.data.local.PrayerDatabase
import com.islamic.prayertimesapp.data.network.ApiService
import com.islamic.prayertimesapp.data.repository.RepositoryImplemtation

import com.islamic.prayertimesapp.domin.repositoery.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(api: ApiService, db: PrayerDatabase, app: Application): repository {
        return RepositoryImplemtation(api, db)
    }


}