package com.license.workguru_app.help_request.domain.use_cases.change_status

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.help_request.data.remote.DTO.ChangeStatusRequest
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository

class ChangeStatusViewModel(val context: Context, val repository: HelpRequestRepository) : ViewModel() {
    suspend fun changeStatus(status_id:Int ):Boolean {

        val access_token = getToken()
        val request = ChangeStatusRequest(status_id)
        try {

            val result = repository.changeStatuses("Bearer " + access_token,request)
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            Log.d("HELP_REQUEST", "${result}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            Log.d("HELP_REQUEST", "ChangeStatusViewModel - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}