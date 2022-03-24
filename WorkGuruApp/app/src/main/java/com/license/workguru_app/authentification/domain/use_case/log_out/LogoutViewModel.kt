package com.license.workguru_app.authentification.domain.use_case.log_out

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.license.workguru_app.authentification.domain.repository.AuthRepository

class LogoutViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {
    suspend fun logout( access_token:String):Boolean {
        try {
            val result = repository.logout()
            Log.d("AUTH", result.message)
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("AUTH", "LogoutViewModel - exception: ${e.toString()}")
            return false
        }
    }
}