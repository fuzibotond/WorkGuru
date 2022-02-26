package com.example.workguru_app.authentification.presentation


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel : ViewModel() {

    val isTermsAndConditionsAccepted = MutableLiveData<Boolean>(false)

    fun acceptTermsAndConditions(isAccepted:Boolean){
        isTermsAndConditionsAccepted.value = isAccepted
    }


}