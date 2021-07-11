package com.clovertech.autolib.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clovertech.autolib.model.Vehicle
import com.clovertech.autolib.network.client.VehicleApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class VehicleViewModel: ViewModel() {

    var vehicleResponse: MutableLiveData<Response<Vehicle>> = MutableLiveData()

    fun getVehicleById(idVehicle: Int) {
        viewModelScope.launch{
            val response = VehicleApiClient.vehicleService.getVehicle(idVehicle)
            vehicleResponse.value = response
        }
    }

}