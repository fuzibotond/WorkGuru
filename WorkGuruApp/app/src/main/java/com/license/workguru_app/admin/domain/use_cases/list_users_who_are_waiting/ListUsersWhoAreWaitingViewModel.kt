package com.license.workguru_app.admin.domain.use_cases.list_users_who_are_waiting

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.admin.data.remote.DTO.WaitingUser
import com.license.workguru_app.admin.data.repository.AdminRepository

class ListUsersWhoAreWaitingViewModel(val context: Context, val repository: AdminRepository) : ViewModel() {
    val userList = MutableLiveData<List<WaitingUser>>()
    suspend fun listUsersWhoAreWaiting(page: Int):Boolean {
        val access_token = getToken()

        try {
            val result = repository.listUsersWhoAreWaiting("Bearer " + access_token, page)
            userList.value = result.data
            Log.d("ADMIN", result.toString())
//            Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.we_can_not_accept), Toast.LENGTH_SHORT).show()
            Log.d("ADMIN", "AcceptUserViewModel - exception: ${e.toString()}")

            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}