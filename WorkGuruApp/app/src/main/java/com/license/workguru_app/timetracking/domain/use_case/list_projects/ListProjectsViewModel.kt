package com.license.workguru_app.timetracking.domain.use_case.list_projects

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.timetracking.data.remote.DTO.Data
import com.license.workguru_app.timetracking.domain.model.Project
import com.license.workguru_app.timetracking.domain.repository.TimeTrackingRepository

class ListProjectsViewModel(val context: Context, val repository: TimeTrackingRepository) : ViewModel() {
    val dataList:MutableLiveData<List<Project>> = MutableLiveData()
    suspend fun listProjects(automatic:Boolean, project_id:String ):Boolean {

        val access_token = getToken()
        try {
            dataList.value = null
            val result = repository.listProjects("Bearer " + access_token, automatic, project_id)
            Log.d("LISTING", result.toString())
            dataList.value = result.data
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("LIST", "ListViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}