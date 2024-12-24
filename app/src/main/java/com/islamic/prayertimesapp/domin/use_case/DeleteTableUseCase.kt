package com.islamic.prayertimesapp.domin.use_case


import com.islamic.prayertimesapp.domin.repositoery.repository
import javax.inject.Inject

class DeleteTableUseCase @Inject constructor(
    private val repository: repository,
) {

    suspend operator fun invoke() {
        repository.deleteAll()
    }
}