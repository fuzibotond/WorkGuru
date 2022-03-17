package com.license.workguru_app.authentification.presentation


import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel : ViewModel() {

    val isTermsAndConditionsAccepted = MutableLiveData<Boolean>(false)
    val signInImageWithFace = MutableLiveData<Bitmap>()

    fun acceptTermsAndConditions(isAccepted:Boolean){
        isTermsAndConditionsAccepted.value = isAccepted
    }
    fun saveImage(image:Bitmap){
        signInImageWithFace.value = image
    }

}