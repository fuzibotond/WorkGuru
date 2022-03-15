package com.license.workguru_app.authentification.domain.use_case.log_in_with_email

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.authentification.data.remote.DTO.LoginRequest
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.utils.Constants


class LoginViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    val grant_type = "password"
    var remember_me:MutableLiveData<Boolean> = MutableLiveData()
    var access_token:MutableLiveData<String> = MutableLiveData()

    suspend fun login():Boolean {
        val request = LoginRequest(
            client_id = Constants.CLIENT_ID,
            client_secret = Constants.CLIENT_SECRET,
            email = email.value!!,
            password = password.value!!,
            grant_type = grant_type,
            remember_me = remember_me.value!! )

        try {
            val result = request.let { repository.login(it) }
            Log.d("AUTH", "Login with email, just made successfully! ${result.access_token} Expires at: ${result.expires_at}")
            if (remember_me.value!!){
                saveUserData(context, access_token = result.access_token, expires_at = result.expires_at)
            }
            val sharedPreferences:SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            access_token.value = sharedPreferences.getString("TOKEN_KEY", null)
            return true
        } catch (e: Exception) {
            Log.d("AUTH", "LoginViewModel - exception: ${e.toString()}")
            return false
        }
    }
}

private fun saveUserData(context: Context, access_token:String, expires_at:String ){
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    Log.d("TIME", "Testing axpires date:${expires_at}")

    editor.apply{
        putString("ACCESS_TOKEN", access_token)
        putString("EXPIRES_AT", expires_at)
    }.apply()
    Toast.makeText(context, "Saved access token data!", Toast.LENGTH_SHORT).show()
}
