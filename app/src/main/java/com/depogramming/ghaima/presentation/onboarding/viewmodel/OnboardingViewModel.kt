package com.depogramming.ghaima.presentation.onboarding.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.onBoarding.LanguageModel
import com.depogramming.ghaima.data.onBoarding.LocationModel
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class OnboardingViewModel(
    val userSettingsRepo: UserSettingsRepo,
    application: Application,
): AndroidViewModel(application) {

    private val _languages= MutableStateFlow<List<LanguageModel>>(listOf())
    val language: StateFlow<List<LanguageModel>> =_languages.asStateFlow()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationState: Location

    private val _askForPermission = MutableSharedFlow<Unit>()
    val askForPermission = _askForPermission.asSharedFlow()
    var enabledLocationSettings: Boolean?=null
    private val _place = MutableStateFlow("")
    val place=_place.asStateFlow()

    val application=getApplication<Application>().applicationContext
    private val locationModel:LocationModel= LocationModel(longitude = 0.0, latitude = 0.0, place = "0")
    init {
        getLanguages()
    }
    lateinit var selectedLanguage: LanguageModel
     fun getLanguages(){
         //actually, here we should get the stored language if any exists
         //but if any exists, how will the user enters that screen?
        viewModelScope.launch {
            _languages.value=userSettingsRepo.getLanguages()
            selectedLanguage=language.value[0]
        }
    }
    fun selectLanguage(languageModel: LanguageModel){
        //TODO call the room or shared shit and store the selection there
        selectedLanguage=languageModel
    }

    fun onLocationPermissionResult(fineLocationGranted: Boolean, coarseLocationGranted: Boolean){
        if(fineLocationGranted||coarseLocationGranted){
            getLocation()
        }
        else{
            //consider showing snack bar that says that we have failed getting the location
        }
    }

    fun requestUserLocation(reset:Boolean){
        if(isLocationProviderEnabled()){
            enabledLocationSettings=null
            if(checkPermissions()){
                getLocation()
            }
            else{
                viewModelScope.launch {
                    _askForPermission.emit(Unit)
                }
            }
        }
        else{
            if(enabledLocationSettings==null||enabledLocationSettings==false) {
                enableLocationService()
                enabledLocationSettings=false
            }
            else{
                enabledLocationSettings=null
            }
            if(reset){
                enabledLocationSettings=null
            }
        }
    }

    fun checkPermissions() =
        application.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                application.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun isLocationProviderEnabled(): Boolean {
        val locationManager: LocationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun enableLocationService() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }
    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                locationState = location
                locationModel.longitude=location.longitude
                locationModel.latitude=location.latitude

                getAddressFromLocation(location)
            } else {
                requestFreshLocation()
            }
        }.addOnFailureListener { e ->
            println("Error getting location: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun requestFreshLocation() {
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(1000).build(),
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    val location = p0.lastLocation
                    if (location != null) {
                        locationState = location
                        locationModel.longitude=location.longitude
                        locationModel.latitude=location.latitude
                        fusedLocationProviderClient.removeLocationUpdates(this)
                        getAddressFromLocation(location)
                    }
                }
            },Looper.getMainLooper()
        )
    }

    fun getAddressFromLocation(location: Location) {
        val geocoder = Geocoder(application, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                if (addresses.isNotEmpty()) {
                    val subAdmin = addresses[0].subAdminArea
                    val admin=addresses[0].adminArea
                    val country=addresses[0].countryName

                    locationModel.place = "$subAdmin \n $admin, $country"
                }
            }
        }
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val subAdmin = addresses[0].subAdminArea
            val admin=addresses[0].adminArea
            val country=addresses[0].countryName

            locationModel.place = "$subAdmin \n $admin, $country"
        } else {
            locationModel.place = "${locationModel.longitude} ${locationModel.latitude} "
        }
        _place.value=locationModel.place
    }


}

@Suppress("UNCHECKED_CAST")
class OnboardingViewModelFactory( private val repository: UserSettingsRepo,private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingViewModel(repository,application) as T
    }
}