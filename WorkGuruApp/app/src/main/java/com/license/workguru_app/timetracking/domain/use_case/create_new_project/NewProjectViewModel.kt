package com.license.workguru_app.timetracking.domain.use_case.create_new_project

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.timetracking.data.remote.DTO.ProjecttRequest
import com.license.workguru_app.timetracking.data.repository.TimeTrackingRepository

class NewProjectViewModel(val context: Context, val repository: TimeTrackingRepository) : ViewModel() {
    suspend fun createNewProject(category_name:String, name:String ):Boolean {

        val access_token = getToken()
        try {
            val request = ProjecttRequest(category_name, name)
            val result = repository.newProject("Bearer " + access_token, request = request)
            Log.d("NEW", result.toString())
            return true
        } catch (e: Exception) {
            if (e.message?.takeLast(4) == "400 "){
                Toast.makeText(context, "This project name already exist under this category!", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(context, "Something went wrong! Try again!", Toast.LENGTH_SHORT).show()

            }
            Log.d("NEW", "NewProjectViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}