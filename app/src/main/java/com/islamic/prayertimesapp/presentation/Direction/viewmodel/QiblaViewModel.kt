package com.islamic.prayertimesapp.presentation.Direction.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.islamic.prayertimesapp.common.util.Resource
import com.islamic.prayertimesapp.data.models.qibla.qiblaResponse
import com.islamic.prayertimesapp.domin.use_case.PrayerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val prayerUseCase: PrayerUseCases,
) : ViewModel() {

    //livedata objects
    private val _qiblaDirection = MutableLiveData<Resource<qiblaResponse>>()
    val qiblaDirection: LiveData<Resource<qiblaResponse>> = _qiblaDirection

    fun getDirection(lat: Double, long: Double) {
        viewModelScope.launch {
            try {
                val response = prayerUseCase.getQiblaDirectionUseCase(lat, long)
                _qiblaDirection.postValue(getDirectionHandler(response))
            } catch (t: Throwable) {
                _qiblaDirection.postValue(Resource.Error(t.message, null))
            }
        }
    }


    private fun getDirectionHandler(response: Response<qiblaResponse>): Resource<qiblaResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message(), null)
    }


}