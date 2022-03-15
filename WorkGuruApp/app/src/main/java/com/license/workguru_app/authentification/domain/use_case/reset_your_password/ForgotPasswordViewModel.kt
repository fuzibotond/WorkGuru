package com.license.workguru_app.authentification.domain.use_case.reset_your_password

import android.content.Context
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.authentification.data.remote.DTO.ForgotPasswordRequest
import com.license.workguru_app.authentification.data.remote.DTO.ForgotPasswordResponse
import com.license.workguru_app.authentification.domain.repository.AuthRepository

class ForgotPasswordViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {
    suspend fun forgotPassword(email: String):Boolean {

        val request = ForgotPasswordRequest(email)

        try {
            val result = repository.forgotPassword(request)
            Log.d("AUTH", result.message)
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {

            Log.d("AUTH", "ForgotPasswordViewModel - exception: ${e.toString()}")

            return false
        }
    }
}

