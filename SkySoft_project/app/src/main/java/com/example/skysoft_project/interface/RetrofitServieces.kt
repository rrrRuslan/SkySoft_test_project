package com.example.skysoft_project.`interface`

import com.example.skysoft_project.model.ATM
import com.example.skysoft_project.model.Terminal
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {

    @GET("infrastructure?json&atm&address=&")
    fun getATMs(@Query("city", encoded = true) city: String): Call<ATM>

    @GET("infrastructure?json&tso&address=&city=")
    fun getTSOs(@Query("city", encoded = true) city: String): Call<Terminal>

}