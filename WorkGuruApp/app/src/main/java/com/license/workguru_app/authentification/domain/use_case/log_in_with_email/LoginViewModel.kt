package com.license.workguru_app.authentification.domain.use_case.log_in_with_email

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.license.workguru_app.R
import com.license.workguru_app.authentification.data.remote.DTO.LoginRequest
import com.license.workguru_app.authentification.data.repository.AuthRepository
import com.license.workguru_app.di.SessionManager
import com.license.workguru_app.utils.Constants


class LoginViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    val grant_type = "password"
    var remember_me:MutableLiveData<Boolean> = MutableLiveData()
    var access_token:MutableLiveData<String> = MutableLiveData()
    lateinit var sessionManager: SessionManager

    suspend fun login(userName:String, userEmail:String):Boolean {
        sessionManager = SessionManager(context)
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

            saveUserData(context, access_token = result.access_token, expires_at = result.expires_at, name = userName.takeWhile { it!='@' }, email = userEmail)
            sessionManager.saveAuthToken(result.access_token)
            if(remember_me.value == true){

            }

            val sharedPreferences:SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            access_token.value = sharedPreferences.getString("TOKEN_KEY", null)
            return true
        } catch (e: Exception) {
            Log.d("AUTH", "LoginViewModel - exception: ${e.toString()}")
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            return false
        }
    }
}

private fun saveUserData(context: Context, access_token:String, expires_at:String, name:String, email:String ){
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    editor.apply{
        putString("ACCESS_TOKEN", access_token)
        putString("USER_NAME", name)
        putString("USER_EMAIL", email)
        putString("EXPIRES_AT", expires_at)
    }.apply()
    Log.d("AUTH", "Saved access token data! Expires date:${expires_at}")
}
