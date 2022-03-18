package com.license.workguru_app.authentification.domain.use_case.log_in_with_google

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.authentification.data.remote.DTO.GoogleRequest
import com.license.workguru_app.authentification.domain.repository.AuthRepository

class GoogleLoginViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {

    var access_token: MutableLiveData<String> = MutableLiveData()

    suspend fun googleLogin(token:String):Boolean {
        val request = GoogleRequest(
            token = token)
        try {
            val result = request.let { repository.googleLogin(it) }
            Log.d("AUTH", "Login with google, just made successfully! ${result.access_token} Expires at: ${result.expires_at}")
            saveUserData(context, access_token = result.access_token, expires_at = result.expires_at)
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            access_token.value = sharedPreferences.getString("TOKEN_KEY", null)
            Log.d("AUTH", "Still have the token: ${access_token.value}")
            return true
        } catch (e: Exception) {
            Log.d("AUTH", "GoogleLoginViewModel - exception: ${e.toString()}")
            return false
        }
    }
}

private fun saveUserData(context: Context, access_token:String, expires_at:String ){
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()


    editor.apply{
        putString("ACCESS_TOKEN", access_token)
        putString("EXPIRES_AT", expires_at)
    }.apply()
    Log.d("AUTH", "Saved access token data! Expires date:${expires_at}")
}