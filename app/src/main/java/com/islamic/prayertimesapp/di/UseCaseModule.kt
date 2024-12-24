package com.islamic.prayertimesapp.di


import com.islamic.prayertimesapp.domin.repositoery.repository
import com.islamic.prayertimesapp.domin.use_case.DeleteTableUseCase
import com.islamic.prayertimesapp.domin.use_case.GetAllPrayersTimesUseCase
import com.islamic.prayertimesapp.domin.use_case.GetPrayersTimesUseCase
import com.islamic.prayertimesapp.domin.use_case.GetQiblaDirectionUseCase
import com.islamic.prayertimesapp.domin.use_case.PrayerUseCases
import com.islamic.prayertimesapp.domin.use_case.SavePrayersTimesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {


    @Provides
    @Singleton
    fun provideUseCase(repository: repository): PrayerUseCases {
        return PrayerUseCases(
            DeleteTableUseCase(repository),
            GetAllPrayersTimesUseCase(repository),
            GetPrayersTimesUseCase(repository),
            GetQiblaDirectionUseCase(repository),
            SavePrayersTimesUseCase(repository),
        )
    }

}