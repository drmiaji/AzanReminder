package com.islamic.prayertimesapp.domin.use_case

import androidx.lifecycle.LiveData

import com.islamic.prayertimesapp.data.models.PrayerTimeResponse
import com.islamic.prayertimesapp.domin.repositoery.repository
import javax.inject.Inject

class GetAllPrayersTimesUseCase  @Inject constructor(
    private val repository: repository,
) {

    operator fun invoke(
    ): LiveData<PrayerTimeResponse> {
        return repository.getAllPrayersTimes()
    }
}