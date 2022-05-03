package com.license.workguru_app.di


import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.remote.DTO.MonthlyHistory
import com.license.workguru_app.profile.data.remote.DTO.ProjectHistory
import com.license.workguru_app.profile.data.remote.DTO.TodaysHistory
import com.license.workguru_app.profile.data.remote.DTO.WeeklyHistory
import com.license.workguru_app.timetracking.data.remote.DTO.Category
import com.license.workguru_app.timetracking.data.remote.DTO.StartStopTimerRequest
import com.license.workguru_app.timetracking.data.remote.DTO.StartTimerResponse
import java.util.*


class SharedViewModel : ViewModel() {

    val isTermsAndConditionsAccepted = MutableLiveData<Boolean>(false)
    val signInImageWithFace = MutableLiveData<Bitmap>()
    val searchingKeyword = MutableLiveData<String>()
    val choosenCategory = MutableLiveData<Category>()
    val numberOfWantedMembers = MutableLiveData<Int>()
    val startedAtDate = MutableLiveData<Long>()
    val chartData = MutableLiveData<List<ProjectHistory>>()
    val todaysHistory = MutableLiveData<TodaysHistory>()
    val weeklyHistory = MutableLiveData<WeeklyHistory>()
    val monthlyHistory = MutableLiveData<MonthlyHistory>()
    val isTimerPaused = MutableLiveData(false)
    val isTimerStarted = MutableLiveData(false)
    val currentProject = MutableLiveData<StartTimerResponse>()

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
    fun saveChartData(data:List<ProjectHistory>){
        chartData.value = data
    }
    fun saveTodaysData(data:TodaysHistory){
        todaysHistory.value = data
    }
    fun saveWeeklyData(data:WeeklyHistory){
        weeklyHistory.value = data
    }
    fun saveMonthlyData(data:MonthlyHistory){
        monthlyHistory.value = data
    }
    fun saveTimerPauseState(state:Boolean){
        isTimerPaused.value = state
    }
    fun saveTimerState(state:Boolean){
        isTimerStarted.value = state
    }
    fun saveCurrentTimer(current:StartTimerResponse){
        currentProject.value = current
    }
}