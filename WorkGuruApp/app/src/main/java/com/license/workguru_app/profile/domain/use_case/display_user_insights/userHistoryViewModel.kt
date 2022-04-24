package com.license.workguru_app.profile.domain.use_case.display_user_insights

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.profile.data.remote.DTO.ProjectHistory
import com.license.workguru_app.profile.domain.repository.ProfileRepository
import com.license.workguru_app.timetracking.data.remote.DTO.Data
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository

class UserHistoryViewModel(val context: Context, val repository: ProfileRepository) : ViewModel() {
    val dataList:MutableLiveData<List<ProjectHistory>> = MutableLiveData()
    suspend fun listUserHistory( ):Boolean {

        val access_token = getToken()
        try {

            val result = repository.listUserHistory("Bearer " + access_token)
            Log.d("HISTORY", "${result}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("HISTORY", "userHistoryVM - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}