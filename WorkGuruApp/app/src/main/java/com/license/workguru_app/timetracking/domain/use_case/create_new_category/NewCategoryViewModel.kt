package com.license.workguru_app.timetracking.domain.use_case.create_new_category

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.timetracking.data.remote.DTO.CategoryRequest
import com.license.workguru_app.timetracking.data.repository.TimeTrackingRepository

class NewCategoryViewModel(val context: Context, val repository: TimeTrackingRepository) : ViewModel() {
    suspend fun createNewCategory(name:String):Boolean {

        val access_token = getToken()
        try {
            val request = CategoryRequest(name)
            val result = repository.newCategory("Bearer " + access_token, request = request)
            Log.d("NEW", result.toString())
            return true
        } catch (e: Exception) {
            if (e.message?.takeLast(4) == "404 "){
                Toast.makeText(context, "This category name is already taken! Please try something else!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Something went wrong! Please try again!", Toast.LENGTH_SHORT).show()
            }
            Log.d("NEW", "NewCategoryViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}