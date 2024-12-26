package com.islamic.prayertimesapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.islamic.prayertimesapp.data.models.PrayerTimeResponse
import com.islamic.prayertimesapp.data.models.qibla.Data
import com.islamic.prayertimesapp.data.models.qibla.qiblaResponse
import com.islamic.prayertimesapp.domin.repositoery.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import retrofit2.Response

class FakeRepositoryImplementation : repository {

    private var response = PrayerTimeResponse(1,5, listOfNotNull(),"test")
    private val observablePrayerTimes = MutableLiveData<PrayerTimeResponse>(response)

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    private fun refreshLiveData() {
        observablePrayerTimes.postValue(response)
    }




    override suspend fun getPrayerTimes(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method: Int,
    ): Response<PrayerTimeResponse> {
        if (shouldReturnNetworkError) {
            return Response.error(500, ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                "Network Error"
            ))
        }

        // Simulate a successful API response
        val prayerTimeResponse = PrayerTimeResponse(1, 5, listOfNotNull(), "test")
        return Response.success(prayerTimeResponse)

    }

    override suspend fun getQiblaDirection(
        latitude: Double,
        longitude: Double,
    ): Response<qiblaResponse> {
        if (shouldReturnNetworkError) {
            return Response.error(500, ResponseBody.create(
                "application/json".toMediaTypeOrNull(),
                "Network Error"
            ))
        }

        // Simulate a successful API response
        val qiblaResponse = qiblaResponse(1, data = Data(642.56 , 55.6846 , 56.56),"test")
        return Response.success(qiblaResponse)
    }


    override suspend fun savePrayersTimes(response: PrayerTimeResponse): Long {
        this.response = PrayerTimeResponse(1,5, listOfNotNull(),"test")
        refreshLiveData()
        return response.id!!.toLong()
    }

    override fun getAllPrayersTimes(): LiveData<PrayerTimeResponse> {
        return observablePrayerTimes
    }

    override suspend fun deleteAll() {
        response = PrayerTimeResponse(0,0, listOfNotNull(),"")
        refreshLiveData()
    }
}