package com.islamic.prayertimesapp.data.repository

import androidx.lifecycle.LiveData
import com.islamic.prayertimesapp.data.local.PrayerDatabase
import com.islamic.prayertimesapp.data.models.PrayerTimeResponse
import com.islamic.prayertimesapp.data.models.qibla.qiblaResponse
import com.islamic.prayertimesapp.data.network.ApiService
import com.islamic.prayertimesapp.domin.repositoery.repository

import retrofit2.Response

import javax.inject.Inject

class RepositoryImplemtation @Inject constructor(
    private val apiInterface: ApiService,
    private val db: PrayerDatabase,
) : repository {


    override suspend fun getPrayerTimes(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method: Int,
    ): Response<PrayerTimeResponse> =
        apiInterface.getPrayerTimes(year, month, latitude, longitude, method)

    override suspend fun getQiblaDirection(
        latitude: Double,
        longitude: Double,
    ): Response<qiblaResponse> = apiInterface.getQiblaDirection(latitude, longitude)

    override suspend fun savePrayersTimes(response: PrayerTimeResponse): Long =
        db.getPrayerDao().savePrayersTimes(response)


    override fun getAllPrayersTimes(): LiveData<PrayerTimeResponse> =
        db.getPrayerDao().getAllPrayersTimes()

    override suspend fun deleteAll() =
        db.getPrayerDao().deleteAll()


}