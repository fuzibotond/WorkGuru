package com.license.workguru_app.timetracking.domain.use_case.list_categories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.timetracking.data.remote.DTO.Category
import com.license.workguru_app.timetracking.data.repository.TimeTrackingRepository

class ListCategoriesViewModel(val context: Context, val repository: TimeTrackingRepository) : ViewModel() {
    val dataList: MutableLiveData<List<Category>> = MutableLiveData()
    suspend fun listCategories( ):Boolean {

        val access_token = getToken()
        try {
            val result = repository.listCategories("Bearer " + access_token)
            dataList.value = result.data
            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            Log.d("LIST", "ListCategoryViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}