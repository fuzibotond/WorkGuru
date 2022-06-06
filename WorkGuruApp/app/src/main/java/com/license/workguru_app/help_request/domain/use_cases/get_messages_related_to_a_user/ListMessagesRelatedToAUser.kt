package com.license.workguru_app.help_request.domain.use_cases.get_messages_related_to_a_user

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.help_request.data.remote.DTO.ChangeStatusRequest
import com.license.workguru_app.help_request.data.remote.DTO.Message
import com.license.workguru_app.help_request.data.repository.HelpRequestRepository

class ListMessagesRelatedToAUser(val context: Context, val repository: HelpRequestRepository) : ViewModel() {
    val messageList = MutableLiveData<List<Message>>()
    suspend fun listMessagesRelatedToAUser(user_id:Int, page:Int ):Boolean {
        val access_token = getToken()
        try {
            val result = repository.getMessages("Bearer " + access_token, user_id, page)
            messageList.value = result.data
            Log.d("HELP_REQUEST", "${result}")
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("HELP_REQUEST", "ListMessagesRelatedToAUser - exception: ${e.toString()}")
            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}