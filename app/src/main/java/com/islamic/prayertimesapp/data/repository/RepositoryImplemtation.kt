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

// get prayer times
    override suspend fun getPrayerTimes(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method: Int,
    ): Response<PrayerTimeResponse> =
        apiInterface.getPrayerTimes(year, month, latitude, longitude, method)

    // get qibla direction
    override suspend fun getQiblaDirection(
        latitude: Double,
        longitude: Double,
    ): Response<qiblaResponse> = apiInterface.getQiblaDirection(latitude, longitude)

    // save data in cache prayersTimes
    override suspend fun savePrayersTimes(response: PrayerTimeResponse): Long =
        db.getPrayerDao().savePrayersTimes(response)

// restart all cache prayersTime
    override fun getAllPrayersTimes(): LiveData<PrayerTimeResponse> =
        db.getPrayerDao().getAllPrayersTimes()

    // حذف كل cache
    override suspend fun deleteAll() =
        db.getPrayerDao().deleteAll()


}