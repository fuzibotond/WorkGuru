package com.license.workguru_app.di


import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.timetracking.data.remote.DTO.Category
import java.util.*


class SharedViewModel : ViewModel() {

    val isTermsAndConditionsAccepted = MutableLiveData<Boolean>(false)
    val signInImageWithFace = MutableLiveData<Bitmap>()
    val searchingKeyword = MutableLiveData<String>()
    val choosenCategory = MutableLiveData<Category>()
    val numberOfWantedMembers = MutableLiveData<Int>()
    val startedAtDate = MutableLiveData<Long>()

    fun acceptTermsAndConditions(isAccepted:Boolean){
        isTermsAndConditionsAccepted.value = isAccepted
    }
    fun saveImage(image:Bitmap){
        signInImageWithFace.value = image
    }
    fun searchWithKeyword(text: String) {
        searchingKeyword.value = text
    }
    fun saveFilterResult(category: Category, numMember:Int, startDate:Long) {
        choosenCategory.value = category
        numberOfWantedMembers.value = numMember
        startedAtDate.value = startDate
    }


}