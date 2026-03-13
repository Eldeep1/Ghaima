package com.depogramming.ghaima.presentation.utils.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import com.depogramming.ghaima.data.weather.model.LocationModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class LocationHelper(private val application: Application) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    suspend fun getCurrentLocation(): LocationResult {

        val hasAccessFineLocationPermission = application.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = application.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasAccessFineLocationPermission && !hasAccessCoarseLocationPermission) {
            return LocationResult.MissingPermission
        }


        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled) {
            return LocationResult.GpsDisabled
        }


        val rawLocation = getRawCoordinates() ?: return LocationResult.Error("Could not retrieve location.")


        val addressString = getAddressFromLocation(rawLocation.latitude, rawLocation.longitude)


        val locationModel = LocationModel(rawLocation.latitude, rawLocation.longitude, addressString)
        return LocationResult.Success(locationModel)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getRawCoordinates(): Location? {
        return suspendCancellableCoroutine { continuation ->
            val locationTask = fusedLocationClient.lastLocation

            locationTask.addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    fusedLocationClient.requestLocationUpdates(
                        LocationRequest.Builder(1000).build(),
                        object : LocationCallback() {
                            override fun onLocationResult(p0: com.google.android.gms.location.LocationResult) {
                                val freshLocation = p0.lastLocation
                                fusedLocationClient.removeLocationUpdates(this)

                                continuation.resume(freshLocation)
                            }
                        }, Looper.getMainLooper()
                    )
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    suspend fun getAddressFromLocation(lat: Double, lon: Double): String {
        val geocoder = Geocoder(application, Locale.getDefault())

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(lat, lon, 1) { addresses ->
                        continuation.resume(formatAddress(addresses, lat, lon))
                    }
                }
            } else {
                withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    formatAddress(addresses, lat, lon)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "$lat, $lon"
        }
    }

    private fun formatAddress(addresses: List<Address>?, lat: Double, lon: Double): String {
        if (addresses.isNullOrEmpty()) return "$lat, $lon"
        val address = addresses[0]

        val validParts = listOfNotNull(
            address.subAdminArea,
            address.adminArea,
            address.countryName
        ).distinct()

        return if (validParts.isNotEmpty()) validParts.joinToString(", ") else "$lat, $lon"
    }
}