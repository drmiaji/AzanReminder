package com.islamic.prayertimesapp.domin.use_case

import com.islamic.prayertimesapp.data.models.PrayerTimeResponse
import com.islamic.prayertimesapp.domin.repositoery.repository
import javax.inject.Inject

class SavePrayersTimesUseCase  @Inject constructor(
    private val repository: repository,
) {

    suspend operator fun invoke(
        response: PrayerTimeResponse,
    ): Long {
        return repository.savePrayersTimes(response)
    }
}