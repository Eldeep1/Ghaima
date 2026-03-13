package com.depogramming.ghaima.presentation.utils.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import com.depogramming.ghaima.data.weather.model.LocationModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class LocationHelper(private val application: Application) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    suspend fun getCurrentLocation(): LocationResult {

        val hasAccessFineLocationPermission =
            application.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission =
            application.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasAccessFineLocationPermission && !hasAccessCoarseLocationPermission) {
            return LocationResult.MissingPermission
        }

        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )

        if (!isGpsEnabled) {
            return LocationResult.GpsDisabled
        }


        return suspendCancellableCoroutine { continuation ->
            @SuppressLint("MissingPermission")
            val locationTask = fusedLocationClient.lastLocation

            locationTask.addOnSuccessListener { location ->
                if (location != null) {

                    val locationModel =
                        getAddressFromLocation(location.latitude, location.longitude)
                    continuation.resume(LocationResult.Success(locationModel))
                } else {
                    fusedLocationClient.requestLocationUpdates(
                        LocationRequest.Builder(1000).build(),
                        object : LocationCallback() {
                            override fun onLocationResult(p0: com.google.android.gms.location.LocationResult) {
                                val location = p0.lastLocation
                                if (location != null) {

                                    fusedLocationClient.removeLocationUpdates(this)
                                    val locationModel = getAddressFromLocation(
                                        location.latitude,
                                        location.longitude
                                    )
                                    continuation.resume(LocationResult.Success(locationModel))
                                }
                            }
                        }, Looper.getMainLooper()
                    )
                }
            }.addOnFailureListener {
                continuation.resume(LocationResult.Error(it.message ?: "Unknown error"))
            }
        }
    }


    private fun getAddressFromLocation(lat: Double, lon: Double): LocationModel {
        val geocoder = Geocoder(application, Locale.getDefault())
        val addressName = try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            formatAddress(addresses, lat, lon)
        } catch (e: Exception) {
            e.printStackTrace()
            "$lat, $lon"
        }

        return LocationModel(latitude = lat, longitude = lon, place = addressName)
    }

    private fun formatAddress(addresses: List<Address>?, lat: Double, lon: Double): String {
        if (addresses.isNullOrEmpty()) return "$lat, $lon"
        val address = addresses[0]
        val validParts =
            listOfNotNull(address.subAdminArea, address.adminArea, address.countryName).distinct()
        return if (validParts.isNotEmpty()) validParts.joinToString(", ") else "$lat, $lon"
    }
}