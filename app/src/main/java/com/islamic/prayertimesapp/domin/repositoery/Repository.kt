package com.islamic.prayertimesapp.domin.repositoery

import androidx.lifecycle.LiveData

import com.islamic.prayertimesapp.data.models.PrayerTimeResponse
import com.islamic.prayertimesapp.data.models.qibla.qiblaResponse
import retrofit2.Response
import retrofit2.http.Path
import retrofit2.http.Query

interface repository {

    suspend fun getPrayerTimes(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method: Int,
    ): Response<PrayerTimeResponse>

    suspend fun getQiblaDirection(
        latitude: Double,
        longitude: Double,
    ): Response<qiblaResponse>

    suspend fun savePrayersTimes(response: PrayerTimeResponse): Long

    fun getAllPrayersTimes(): LiveData<PrayerTimeResponse>

    suspend fun deleteAll()
}