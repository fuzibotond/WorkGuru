package com.license.workguru_app.authentification.domain.use_case.log_out

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.license.workguru_app.authentification.data.remote.DTO.LogoutRequest
import com.license.workguru_app.authentification.domain.repository.AuthRepository
import com.license.workguru_app.utils.Constants


class LogoutViewModel(val context: Context, val repository: AuthRepository) : ViewModel() {
    suspend fun logout( access_token:String):Boolean {
        val request = LogoutRequest(access_token = access_token)
        try {
            val result = repository.logout("Bearer " + access_token)
            Log.d("AUTH", result.message)
            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(Constants.CLIENT_ID_GOOGLE)
                .build()
            // Build a GoogleSignInClient with the options specified by gso.
            val mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
            mGoogleSignInClient.signOut()
            return true
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            Log.d("AUTH", "LogoutViewModel - exception: ${e.toString()}")
            return false
        }
    }
}