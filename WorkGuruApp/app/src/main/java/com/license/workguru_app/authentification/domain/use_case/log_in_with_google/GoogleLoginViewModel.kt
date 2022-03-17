package com.license.workguru_app.authentification.domain.use_case.log_in_with_google

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.authentification.data.remote.DTO.GoogleLoginRequest
import com.license.workguru_app.authentification.data.remote.DTO.LoginRequest
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.utils.Constants

class GoogleLoginViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {

    var access_token: MutableLiveData<String> = MutableLiveData()

    suspend fun googleLogin():Boolean {
        Log.d("AUTH", access_token.value!!)
        val request = GoogleLoginRequest(
            token = access_token.value!!,false)

        try {
            val result = request.let { repository.googleLogin(it) }
            Log.d("AUTH", "Login with google, just made successfully! ${result.access_token} Expires at: ${result.expires_at}")
            saveUserData(context, access_token = result.access_token, expires_at = result.expires_at)
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            access_token.value = sharedPreferences.getString("TOKEN_KEY", null)
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
    Log.d("AUTH", "Testing expires date:${expires_at}")

    editor.apply{
        putString("ACCESS_TOKEN", access_token)
        putString("EXPIRES_AT", expires_at)
    }.apply()
    Toast.makeText(context, "Saved access token data!", Toast.LENGTH_SHORT).show()
}