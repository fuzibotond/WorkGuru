package com.license.workguru_app.di


import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.help_request.data.remote.DTO.Skill
import com.license.workguru_app.profile.data.remote.DTO.MonthlyHistory
import com.license.workguru_app.profile.data.remote.DTO.ProjectHistory
import com.license.workguru_app.profile.data.remote.DTO.TodaysHistory
import com.license.workguru_app.profile.data.remote.DTO.WeeklyHistory
import com.license.workguru_app.timetracking.data.remote.DTO.Category
import com.license.workguru_app.timetracking.data.remote.DTO.StartTimerResponse


class SharedViewModel : ViewModel() {

    val isTermsAndConditionsAccepted = MutableLiveData<Boolean>(false)
    val signInImageWithFace = MutableLiveData<Bitmap>()
    val searchingKeyword = MutableLiveData<String>()
    val choosenCategory = MutableLiveData<Category>()
    val numberOfWantedMembers = MutableLiveData<Int>()
    val startedAtDate = MutableLiveData<Long>()
    val isFiltered = MutableLiveData(false)
    val chartData = MutableLiveData<List<ProjectHistory>>()
    val todaysHistory = MutableLiveData<TodaysHistory>()
    val weeklyHistory = MutableLiveData<WeeklyHistory>()
    val monthlyHistory = MutableLiveData<MonthlyHistory>()
    val isTimerPaused = MutableLiveData(false)
    val isTimerStarted = MutableLiveData(false)
    val currentProject = MutableLiveData<StartTimerResponse>()
    val pomodoroIsON = MutableLiveData<Boolean>(false)
    val pomodoroNotification = MutableLiveData<Boolean>(false)
    val numOfPomodoroSession = MutableLiveData<Int>()
    val numOfSessionDuration = MutableLiveData<Int>()
    val isColleaguesFilterActive = MutableLiveData<Boolean>(false)
    val skillToFilter = MutableLiveData<String>()
    val statusToFilter = MutableLiveData<String>()
    val minNumberOfWorkHour = MutableLiveData<Int>()
    val actualSkills: MutableLiveData<ArrayList<Skill>> = MutableLiveData<ArrayList<Skill>>()
    val profileStatus: MutableLiveData<Skill> = MutableLiveData<Skill>()
    val profileRole: MutableLiveData<String> = MutableLiveData<String>()
    val messageColleagueUserId : MutableLiveData<Int> = MutableLiveData<Int>()
    val messageColleagueUserName : MutableLiveData<String> = MutableLiveData<String>()

    fun acceptTermsAndConditions(isAccepted:Boolean){
        isTermsAndConditionsAccepted.value = isAccepted
    }
    fun saveImage(image:Bitmap){
        signInImageWithFace.value = image
    }
    fun searchWithKeyword(text: String) {
        searchingKeyword.value = text
    }
    fun saveFilterResult(category: Category?, numMember:Int, startDate:Long, isFilteredByAll:Boolean) {
        choosenCategory.value = category!!
        numberOfWantedMembers.value = numMember
        startedAtDate.value = startDate
        isFiltered.value = isFilteredByAll
    }
    fun saveColleagueFilterCriteria(skill: String?, minWorkHour:Int, status:String, isFilterActive:Boolean) {
        skillToFilter.value = skill!!
        statusToFilter.value = status
        minNumberOfWorkHour.value = minWorkHour
        isColleaguesFilterActive.value = isFilterActive
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
    fun saveCurrentTimer(current: StartTimerResponse?){
        currentProject.value = current
    }
    fun savePomodoroState(isOn:Boolean){
        pomodoroIsON.value = isOn
    }
    fun savePomodoroNotification(isOn: Boolean){
        pomodoroNotification.value = isOn
    }
    fun saveNumberOfSession(sessions:Int){
        numOfPomodoroSession.value = sessions
    }
    fun saveDurationOfAPomodoroSession(durationInMinutes:Int){
        numOfSessionDuration.value = durationInMinutes
    }
    fun saveSkills(skillList: ArrayList<Skill>){
        actualSkills.value = skillList
    }
    fun saveStatus(status:Skill){
        profileStatus.value = status
    }
    fun saveProfileRole(role:String){
        profileRole.value = role
    }
    fun saveMessageColleagueUserId(user_id: Int){
        messageColleagueUserId.value = user_id
    }
    fun saveMessageColleagueUserName(user_name: String){
        messageColleagueUserName.value = user_name
    }


}