package com.example.skysoft_project.controller

import com.example.skysoft_project.`interface`.RetrofitServices
import com.example.skysoft_project.retrofit.RetrofitClient

object Controller {

    private val BASE_URL = "https://api.privatbank.ua/p24api/"
    //https://api.privatbank.ua/p24api/infrastructure?json&atm&address=&city=Днепропетровск


    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}