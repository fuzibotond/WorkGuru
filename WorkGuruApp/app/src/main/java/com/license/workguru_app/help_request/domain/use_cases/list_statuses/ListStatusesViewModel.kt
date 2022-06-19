package com.license.workguru_app.help_request.domain.use_cases.list_statuses

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.help_request.data.remote.DTO.Skill
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository

class ListStatusesViewModel(val context: Context, val repository: HelpRequestRepository) : ViewModel() {
    val statusList = MutableLiveData<List<Skill>>()
    suspend fun listStatuses(page:Int ):Boolean {

        val access_token = getToken()
        try {

            val result = repository.getStatuses("Bearer " + access_token, page)
            statusList.value = result.data
            Log.d("HELP_REQUEST", "${result}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            Log.d("HELP_REQUEST", "ListSkillsViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}