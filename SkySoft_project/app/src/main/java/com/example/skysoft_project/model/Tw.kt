package com.example.skysoft_project.model

import com.google.gson.annotations.SerializedName

data class Tw(
    @SerializedName("mon") val mon: String,
    @SerializedName("tue") val tue: String,
    @SerializedName("wed") val wed: String,
    @SerializedName("thu") val thu: String,
    @SerializedName("fri") val fri: String,
    @SerializedName("sat") val sat: String,
    @SerializedName("sun") val sun: String,
    @SerializedName("hol") val hol: String
){
    constructor() : this("","","","","","","","")
}