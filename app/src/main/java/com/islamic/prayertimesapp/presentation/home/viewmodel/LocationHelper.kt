package com.islamic.prayertimesapp.presentation.home.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LocationHelper(private val context: AppCompatActivity) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    interface OnLocationFetchedListener {
        fun onLocationFetched(location: Location?, address: String?)
        fun onError(error: String?)
    }

    fun fetchLocation(onLocationFetchedListener: OnLocationFetchedListener) {
        // Check location permission
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Get the last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location == null) {
                    onLocationFetchedListener.onError("تعذر الحصول على الموقع، يرجى تفعيل GPS.")
                    return@addOnSuccessListener
                }

                val geocoder = Geocoder(context, Locale.getDefault())
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Get the address from location
                        val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        } else {
                            @Suppress("DEPRECATION")
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        }

                        if (addresses.isNullOrEmpty()) {
                            withContext(Dispatchers.Main) {
                                onLocationFetchedListener.onError("لم يتم العثور على عنوان.")
                            }
                            return@launch
                        }

                        val addressText = addresses[0].getAddressLine(0)

                        withContext(Dispatchers.Main) {
                            onLocationFetchedListener.onLocationFetched(location, addressText)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onLocationFetchedListener.onError("فشل في جلب العنوان: ${e.message}")
                        }
                    }
                }
            }
        } else {
            // Request permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                onLocationFetchedListener.onError("تحتاج هذه الميزة إلى صلاحيات الموقع.")
            } else {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}
