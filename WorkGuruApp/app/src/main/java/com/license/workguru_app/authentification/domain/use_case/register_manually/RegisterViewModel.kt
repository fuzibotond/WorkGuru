package com.license.workguru_app.authentification.domain.use_case.register_manually

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.authentification.data.remote.DTO.RegisterRequest
import com.license.workguru_app.authentification.data.repository.AuthRepository

class RegisterViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {
    suspend fun signUp( email: String,
                                name: String,
                                password: String):Boolean {

        val request = RegisterRequest(email = email, name = name, password = password)

        try {
            val result = repository.register(request)
            Log.d("AUTH", result.message)
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            Log.d("AUTH", "RegisterViewModel - exception: ${e.toString()}")

            return false
        }
    }
}