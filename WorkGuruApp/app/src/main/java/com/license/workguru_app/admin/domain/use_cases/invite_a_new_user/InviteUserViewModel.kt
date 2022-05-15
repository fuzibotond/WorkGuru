package com.license.workguru_app.admin.domain.use_cases.invite_a_new_user

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.admin.data.remote.DTO.InviteUserRequest
import com.license.workguru_app.admin.domain.repository.AdminRepository
import com.license.workguru_app.authentification.data.remote.DTO.ForgotPasswordRequest
import com.license.workguru_app.authentification.domain.repository.AuthRepository

class InviteUserViewModel(val context: Context, val repository: AdminRepository) : ViewModel() {
    suspend fun inviteUser(email: String):Boolean {
        val access_token = getToken()
        val request = InviteUserRequest(email)

        try {
            val result = repository.inviteUser("Bearer " + access_token, request)
            Log.d("INVITE", result.message)
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "The given email already exist in the database or not correct. Try something else...", Toast.LENGTH_SHORT).show()
            Log.d("INVITE", "InviteUserViewModel - exception: ${e.toString()}")

            return false
        }
    }
    fun getToken(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        return savedToken
    }
}

