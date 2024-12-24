package com.islamic.prayertimesapp.domin.use_case

import com.islamic.prayertimesapp.data.models.PrayerTimeResponse
import com.islamic.prayertimesapp.domin.repositoery.repository
import retrofit2.Response
import javax.inject.Inject

class GetPrayersTimesUseCase @Inject constructor(
    private val repository: repository,
) {

    suspend operator fun invoke(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method: Int,
    ): Response<PrayerTimeResponse> {
        return repository.getPrayerTimes(year, month, latitude, longitude, method)
    }
}
