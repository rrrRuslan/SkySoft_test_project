package com.example.skysoft_project.model

import com.google.gson.annotations.SerializedName

data class Device(

        @SerializedName("type") val type : String,
        @SerializedName("latitude") val latitude : Double,
        @SerializedName("longitude") val longitude : Double,
        @SerializedName("tw") val tw : Tw,
        @SerializedName("fullAddressRu") val fullAddressRu : String,
        @SerializedName("fullAddressUa") val fullAddressUa : String,
        @SerializedName("fullAddressEn") val fullAddressEn : String,
        @SerializedName("placeRu") val placeRu : String,
        @SerializedName("placeUa") val placeUa : String,
        @SerializedName("cityRU") val cityRU : String,
        @SerializedName("cityUA") val cityUA : String,
        @SerializedName("cityEN") val cityEN : String
){
        constructor() : this("", 0.0 , 0.0 , Tw(), "","","","","","","","",)
}