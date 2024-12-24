package com.islamic.prayertimesapp.domin.use_case


import com.islamic.prayertimesapp.data.models.qibla.qiblaResponse
import com.islamic.prayertimesapp.domin.repositoery.repository
import kotlinx.coroutines.CoroutineScope
import retrofit2.Response
import javax.inject.Inject

class GetQiblaDirectionUseCase @Inject constructor(
    private val repository: repository,
) {
     suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
    ): Response<qiblaResponse> {
        return repository.getQiblaDirection(latitude, longitude)
    }
}