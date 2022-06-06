package com.license.workguru_app.timetracking.domain.use_case.list_projects

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.timetracking.data.remote.DTO.ShortProject
import com.license.workguru_app.timetracking.domain.model.Project
import com.license.workguru_app.timetracking.data.repository.TimeTrackingRepository

class ListProjectsViewModel(val context: Context, val repository: TimeTrackingRepository) : ViewModel() {
    val dataList:MutableLiveData<List<Project>> = MutableLiveData()
    val filteredList:MutableLiveData<List<Project>> = MutableLiveData()
    val fullList:MutableLiveData<List<ShortProject>> = MutableLiveData()
    suspend fun listProjects( category_id:String ):Boolean {

        val access_token = getToken()
        try {
            filteredList.value = null
            val result = repository.listProjects("Bearer " + access_token, category_id)
            Log.d("FILTER", result.toString())
            filteredList.value = result.data
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("FILTER", "listProjects - exception: ${e.toString()}")
            return false
        }
    }
    suspend fun listProjectsByPage( page:Int ):Boolean {
        val access_token = getToken()
        try {
            dataList.value = null
            val result = repository.listProjectsByPage("Bearer " + access_token, page)
            Log.d("LOADING", result.toString())
            dataList.value = result.data
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("LOADING", "listProjectsByPage - exception: ${e.toString()}")
            return false
        }
    }
    suspend fun listAllProjects(automatic:Boolean, project_id:String ):Boolean {

        val access_token = getToken()
        try {
            dataList.value = null
            val result = repository.listAllProjects("Bearer " + access_token, automatic, project_id)
            Log.d("LISTING", result.toString())
            dataList.value = result.data
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("LIST", "listAllProjects - exception: ${e.toString()}")
            return false
        }
    }
    suspend fun listAllProjectsWithoutPagination(pagination:Boolean ):Boolean {
        val access_token = getToken()
        try {
            fullList.value = null
            val result = repository.listAllProjectsWithoutPagination("Bearer " + access_token, pagination)
            Log.d("LISTING", result.toString())
            fullList.value = result.data
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("LIST", "listAllProjectsWithoutPagination - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}