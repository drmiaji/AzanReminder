package com.islamic.prayertimesapp.data.network


import com.islamic.prayertimesapp.data.models.PrayerTimeResponse
import com.islamic.prayertimesapp.data.models.qibla.qiblaResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("calendar/{year}/{month}")
    suspend fun getPrayerTimes(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
    ): Response<PrayerTimeResponse>

    @GET("qibla/{latitude}/{longitude}")
    suspend fun getQiblaDirection(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double,
    ): Response<qiblaResponse>


}