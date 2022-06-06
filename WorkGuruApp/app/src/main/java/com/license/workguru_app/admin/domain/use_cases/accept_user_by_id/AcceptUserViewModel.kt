package com.license.workguru_app.admin.domain.use_cases.accept_user_by_id

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.admin.data.remote.DTO.InviteUserRequest
import com.license.workguru_app.admin.data.repository.AdminRepository

class AcceptUserViewModel(val context: Context, val repository: AdminRepository) : ViewModel() {
    suspend fun AcceptUser(user_id: Int):Boolean {
        val access_token = getToken()

        try {
            val result = repository.acceptUser("Bearer " + access_token, user_id)
            Log.d("ADMIN", result.message)
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
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
