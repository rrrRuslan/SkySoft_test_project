package com.example.skysoft_project;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skysoft_project.model.ATM
import com.example.skysoft_project.model.Terminal

class InfoViewModel: ViewModel() {

    var atm = MutableLiveData<ATM>()
    var tso = MutableLiveData<Terminal>()

}
