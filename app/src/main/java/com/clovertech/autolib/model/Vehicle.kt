package com.clovertech.autolib.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Vehicle(

    @Expose val idVehicle: Int,
    @Expose val vehiclebrand: String,
    @Expose val vehiclemodel: String,
    @Expose val image: String,
    @Expose val longitude: Double,
    @Expose val latitude: Double,
    @Expose val chassisNumber: String)