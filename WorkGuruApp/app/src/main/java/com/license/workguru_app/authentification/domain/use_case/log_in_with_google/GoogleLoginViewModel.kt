package com.license.workguru_app.authentification.domain.use_case.log_in_with_google

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.license.workguru_app.authentification.data.remote.DTO.GoogleRequest
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.utils.Constants

class GoogleLoginViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {

    var access_token: MutableLiveData<String> = MutableLiveData()

    suspend fun googleLogin(token:String):Boolean {
        Log.d("GOOGLE-SIGN-IN", "Token: ${token}")

        val request = GoogleRequest(
            id_token = token)
        try {
            val result = request.let { repository.googleLogin(it) }
            Log.d("GOOGLE-SIGN-IN", "Login with google, just made successfully! ${result.access_token} Expires at: ${result.expires_at}")
            saveUserData(context, access_token = result.access_token, expires_at = result.expires_at)
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            access_token.value = sharedPreferences.getString("TOKEN_KEY", null)
            Log.d("GOOGLE-SIGN-IN", "Still have the token: ${access_token.value}")
            return true
        } catch (e: Exception) {
            Log.d("GOOGLE-SIGN-IN", "GoogleLoginViewModel - exception: ${e.toString()}")
            var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(Constants.CLIENT_ID_GOOGLE)
                .build()
            // Build a GoogleSignInClient with the options specified by gso.
            val mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
            mGoogleSignInClient.signOut()
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