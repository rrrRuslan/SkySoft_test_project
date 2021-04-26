package com.example.skysoft_project.model

import com.google.gson.annotations.SerializedName

data class Terminal(
        @SerializedName("devices")
        val devices: List<Device>,

        @SerializedName("address")
        val address: String,

        @SerializedName("city")
        val city: String
)


